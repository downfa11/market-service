package com.ns.marketservice.Service;

import com.ns.marketservice.Domain.*;
import com.ns.marketservice.Domain.DTO.*;
import com.ns.marketservice.Repository.BoardImageRepository;
import com.ns.marketservice.Repository.BoardRepository;
import com.ns.marketservice.Repository.CategoryRepository;
import com.ns.marketservice.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class BoardService {

    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final BoardImageRepository boardImageRepository;
    private final CategoryRepository categoryRepository;

    public boolean validateBoard(Long idx,Long boardId){
        Optional<Board> optBoard = boardRepository.findByBoardId(boardId);
        if(optBoard.isPresent()){
            Membership member = optBoard.get().getMembership();
            log.info(member.getMembershipId()+" "+idx);
            if(member.isValid() && member.getMembershipId().equals(idx))
                return true;
        }
        return false;
    }

    public BoardResponse toBoardResponse(Board board){
        BoardResponse boardResponse = new BoardResponse();
        boardResponse.setBoardId(board.getBoardId());
        boardResponse.setCategoryId(board.getCategory().getId());
        boardResponse.setRegion(board.getRegion());
        boardResponse.setNickname(board.getMembership().getNickname());
        boardResponse.setContents(board.getContents());
        boardResponse.setHits(board.getHits());
        boardResponse.setCreatedAt(board.getCreatedAt());
        boardResponse.setUpdatedAt(board.getUpdatedAt());
        boardResponse.setBoardImageUrl(board.getBoardImageUrl());
        boardResponse.setSortStatus(board.getSortStatus());
        return boardResponse;
    }


    public messageEntity getMyBoards(Long idx,Long offset) {
            Optional<Membership> memberOptional = userRepository.findById(idx);

            if (memberOptional.isPresent()) {
                Membership membership = memberOptional.get();
                String writer = membership.getNickname();

                BoardFilter filter = new BoardFilter();
                filter.setSortStatus(new ArrayList<>());
                filter.setRegions(new ArrayList<>());
                filter.setCategories(new ArrayList<>());

                return new messageEntity("Success", searchBoardByUserNickname(writer,offset,filter));
            } else {
                return new messageEntity("Fail","membership not exist.");
            }

    }


    public messageEntity getBoardByBoardId(Long boardId, Long idx) {
            Optional<Board> findBoard = boardRepository.findByBoardId(boardId);

            if (findBoard.isPresent()) {
                Board board = findBoard.get();
                return new messageEntity("Success",getBoardByBoardIdImplement(boardId,board,idx));

            } else {
                return new messageEntity("Fail","board not exist.");
            }

    }

    @Async
    @Cacheable(value="getPosts",key="'getPosts'+':'+'boardId'+'='+#boardId")
    public BoardResponse getBoardByBoardIdImplement(Long boardId, Board board,Long idx) {
        if (!idx.equals(board.getMembership().getMembershipId()))
            board.setHits(board.getHits() + 1); // 조회수 증가

        boardRepository.save(board);
        return toBoardResponse(board);
    }

    public messageEntity getBoardsAll(Long offset,BoardFilter filter) {

        List<BoardList> boards = getBoardsAllImplement(offset,filter);
        if (boards.isEmpty())
            return new messageEntity("Fail","Category or board not exist.");

        return new messageEntity("Success",boards);
    }

    @Async
    @Cacheable(value="getPosts",key="'getPosts'+':'+ #offset+':'+'sortStatus='+#filter.sortStatus+':'+'regions='+#filter.regions+':'+'categories='+#filter.categories")
    public List<BoardList> getBoardsAllImplement(Long offset,BoardFilter filter) {
        return boardRepository.findBoardAll(offset*10+1,filter);
    }

    public messageEntity searchBoardByUserNickname(String writer,Long offset,BoardFilter filter) {
        List<BoardList> boards = searchBoardByUserNicknameImplement(writer,offset,filter);
            if (boards.isEmpty())
                return new messageEntity("Fail","board not exist.");

        return new messageEntity("Success",boards);

    }

    @Async
    @Cacheable(value="getPosts",key="'getPosts'+':'+ #offset+':'+'writer='+#writer+':'+'sortStatus='+#filter.sortStatus+':'+'regions='+#filter.regions+':'+'categories='+#filter.categories")
    public List<BoardList> searchBoardByUserNicknameImplement(String writer,Long offset,BoardFilter filter) {
        return boardRepository.findByNickname(writer,offset*10+1,filter);
    }

    public messageEntity searchBoardByContent(String keyword,Long offset,BoardFilter filter) {
        List<BoardList> boards = boardRepository.findByContents(keyword,offset*10+1,filter);
            if (boards.isEmpty())
                return new messageEntity("Fail","board not exist.");

        return new messageEntity("Success",boards);


    }

    @Async
    @Cacheable(value="getPosts",key="'getPosts'+':'+ #offset+':'+'keyword='+#keyword+':'+'sortStatus='+#filter.sortStatus+':'+'regions='+#filter.regions+':'+'categories='+#filter.categories")
    public List<BoardList> searchBoardByContentImplement(String keyword,Long offset,BoardFilter filter) {
        return boardRepository.findByContents(keyword,offset*10+1,filter);
    }

    public messageEntity searchBoardByTitle(String title, Long offset, BoardFilter filter) {
        List<BoardList> boards = searchBoardByTitleImplement(title,offset,filter);
        if (boards.isEmpty())
            return new messageEntity("Fail","board not exist.");

        return new messageEntity("Success",boards);
    }

    @Async
    @Cacheable(value="getPosts",key="'getPosts'+':'+ #offset+':'+'title='+#title+':'+'sortStatus='+#filter.sortStatus+':'+'regions='+#filter.regions+':'+'categories='+#filter.categories")
    public  List<BoardList> searchBoardByTitleImplement(String title, Long offset, BoardFilter filter) {
        return boardRepository.findByTitle(title,offset*10+1,filter);
    }

    @CacheEvict(value="getPosts", allEntries = true)
    public messageEntity add(Long idx, postRequest request, List<MultipartFile> images) {
            String contents = request.getContents();
            if (contents == null || contents.equals(""))
                return new messageEntity("Fail","contents is blank.");

            Optional<Membership> optMember = userRepository.findById(idx);

            if (optMember.isPresent()) {
                Membership membership = optMember.get();

                Board board = new Board();
                board.setTitle(request.getTitle());

                Optional<Category> optCategory = categoryRepository.findById(request.getCategoryId());

                if(optCategory.isEmpty())
                    return new messageEntity("Fail","Category not exist.");

                board.setCategory(optCategory.get());
                board.setRegion(request.getRegion());
                board.setContents(request.getContents());
                board.setPrice(request.getPrice());
                board.setCreatedAt(new Timestamp(System.currentTimeMillis()));
                board.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
                board.setMembership(membership);
                board.setHits(0);
                board.setSortStatus(Board.SortStatus.SALE);

                membership.setCurProductRegion(board.getCategory().getCategoryName());
                if (images != null) {
                    for (MultipartFile imageFile : images) {

                        List<BoardImage> savedImages = saveImages(Collections.singletonList(imageFile), board);
                        board.getBoardImage().addAll(savedImages);
                    }
                }

                board.fillBoardImageUrl();
                boardRepository.save(board);

                return new messageEntity("Success",toBoardResponse(board));

            } else {
                return new messageEntity("Fail","Membership not exist.");
            }
        }

    public messageEntity setStatusSold(Long idx,Long boardId){
        if(validateBoard(idx,boardId)){
            Optional<Board> findBoard = this.boardRepository.findByBoardId(boardId);
            if (findBoard.isPresent()) {
                Board board = findBoard.get();
                board.setSortStatus(Board.SortStatus.SOLD);
                boardRepository.save(board);

                return new messageEntity("Success",toBoardResponse(board));

            }
        }
        return new messageEntity("Fail","not Valid boardId and ids");
    }

    public messageEntity setStatusSale(Long idx,Long boardId){
        if(validateBoard(idx,boardId)){
            Optional<Board> findBoard = this.boardRepository.findByBoardId(boardId);
            if (findBoard.isPresent()) {
                Board board = findBoard.get();
                board.setSortStatus(Board.SortStatus.SALE);
                boardRepository.save(board);

                return new messageEntity("Success",toBoardResponse(board));

            }
        }
        return new messageEntity("Fail","not valid boardId to ids");
    }

    @Transactional
    @CacheEvict(value="getPosts", allEntries = true)
    public messageEntity updateBoard(Long boardId, postRequest request, List<MultipartFile> images) {

            Optional<Board> findBoard = boardRepository.findById(boardId);
            if (findBoard.isPresent()) {
                Board board = findBoard.get();
                String contents = request.getContents();
                if (contents != null || contents.equals(""))
                    board.setContents(contents);

                board.setRegion(request.getRegion());

                if (images != null) {

                    for (MultipartFile imageFile : images) {

                        List<BoardImage> savedImages = saveImagesFix(Collections.singletonList(imageFile), board);
                        board.getBoardImage().addAll(savedImages);
                    }

                }

                if(!board.getCategory().equals(request.getCategoryId())){
                    Optional<Category> optCategory = categoryRepository.findById(request.getCategoryId());
                    if(optCategory.isEmpty())
                        return new messageEntity("Fail","Invalid Category.");

                    board.setCategory(optCategory.get());
                }

                board.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
                board.setSortStatus(Board.SortStatus.SALE);

                Membership membership = board.getMembership();
                if(!membership.getCurProductRegion().equals(board.getCategory().getCategoryName()))
                    membership.setCurProductRegion(board.getCategory().getCategoryName());

                board.fillBoardImageUrl();
                boardRepository.save(board);

                return new messageEntity("Success",toBoardResponse(board));

            } else {
                return new messageEntity("Fail","Board is not exist.");
            }

    }

    @CacheEvict(value="getPosts", allEntries = true)
    public messageEntity deleteBoard(Long boardId) {
            Optional<Board> findBoard = boardRepository.findById(boardId);
            if (findBoard.isPresent()) {
                Board board = findBoard.get();

                deleteImagesByBoard(board);
                boardRepository.deleteByBoardId(boardId);
            } else {
                return new messageEntity("Fail","board not exist.");
            }
        return new messageEntity("Success",boardId);
    }

    public List<BoardImage> saveImages(List<MultipartFile> imageFiles, Board board) {
        List<BoardImage> images = new ArrayList<>();

        String projectPath = System.getProperty("user.dir") + "\\src\\main\\resources\\templates\\image\\";


        for (MultipartFile imageFile : imageFiles) {
            UUID uuid = UUID.randomUUID();
            String originalFileName = uuid + "_" + imageFile.getOriginalFilename();
            File saveFile = new File(projectPath + originalFileName);

            BoardImage image = new BoardImage();

            try {
                imageFile.transferTo(saveFile);

                image.setImgName(originalFileName);
                image.setImgOriName(imageFile.getOriginalFilename());
                image.setImgPath(saveFile.getAbsolutePath());
                image.setBoard(board);

                images.add(image);

            } catch (IOException e) {
                throw new RuntimeException("이미지 저장에 실패했습니다. :"+e);
            }
        }
        boardImageRepository.saveAll(images);


        return images;
    }

    public List<BoardImage> saveImagesFix(List<MultipartFile> imageFiles, Board board) {
        List<BoardImage> images = boardImageRepository.findByBoard(board);

        String projectPath = System.getProperty("user.dir") + "\\src\\main\\resources\\templates\\image\\";


        for (MultipartFile imageFile : imageFiles) {
            UUID uuid = UUID.randomUUID();
            String originalFileName = uuid + "_" + imageFile.getOriginalFilename();
            File saveFile = new File(projectPath + originalFileName);

            BoardImage image = new BoardImage();

            try {
                imageFile.transferTo(saveFile);

                image.setImgName(originalFileName);
                image.setImgOriName(imageFile.getOriginalFilename());
                image.setImgPath(saveFile.getAbsolutePath());
                image.setBoard(board);

                images.add(image);

            } catch (IOException e) {
                throw new RuntimeException("이미지 저장에 실패했습니다. :"+e);
            }
        }
        boardImageRepository.saveAll(images);


        return images;
    }

    public void deleteImagesByBoard(Board board) {
        List<BoardImage> images = boardImageRepository.findByBoard(board);

        for (BoardImage image : images)
            boardImageRepository.delete(image);

    }
}