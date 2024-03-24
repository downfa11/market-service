package com.ns.marketservice.Service;

import com.ns.marketservice.Domain.*;
import com.ns.marketservice.Domain.DTO.BoardFilter;
import com.ns.marketservice.Domain.DTO.BoardResponse;
import com.ns.marketservice.Domain.DTO.postRequest;
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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

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

    public List<BoardResponse> getBoards() {
            List<Board> boards = boardRepository.findAll();

            if (boards.isEmpty()) {
                throw new RuntimeException("boards is not exist.");
            }

            List<BoardResponse> boardResponses = new ArrayList<>();
            boards.stream()
                .map(this::toBoardResponse)
                .forEach(boardResponses::add);


            return boardResponses;
    }

    @Async
    @Cacheable(value="getPosts",key="'getPosts'+':'+#idx")
    public List<BoardResponse> getMyBoards(Long idx) {

            Optional<Membership> memberOptional = userRepository.findById(idx);

            if (memberOptional.isPresent()) {
                Membership membership = memberOptional.get();
                List<Board> boards = boardRepository.findBoardByMembership(membership);

                if (boards.isEmpty()) {
                    throw new RuntimeException("boards is not exist.");
                }

                List<BoardResponse> boardResponses = new ArrayList<>();
                boards.stream()
                        .map(this::toBoardResponse)
                        .forEach(boardResponses::add);


                return boardResponses;
            } else {
                throw new RuntimeException("membership not exist.");
            }

    }

    public BoardResponse getBoardByBoardId(Long boardId, Long idx) {
            Optional<Board> findBoard = boardRepository.findByBoardId(boardId);

            if (findBoard.isPresent()) {
                Board board = findBoard.get();
                if (!idx.equals(findBoard.get().getMembership().getMembershipId()))
                    board.setHits(board.getHits() + 1); // 조회수 증가

                boardRepository.save(board);
                return toBoardResponse(board);

            } else {
                throw new RuntimeException("boards is not exist.");
            }

    }

    @Async
    @Cacheable(value="getPosts",key="'getPosts'+':'+ #lastboardId")
    public List<BoardResponse> getBoardsAll(Long lastboardId,BoardFilter filter) {
        int pageSize = 10;

        Page<Board> boards = fetchPagesAll(lastboardId,pageSize,filter);

        if (boards.isEmpty())
            throw new RuntimeException("category or board is not exist.");

        List<BoardResponse> boardResponses = new ArrayList<>();
        boards.stream()
                .map(this::toBoardResponse)
                .forEach(boardResponses::add);

        return boardResponses;

    }

    private Page<Board> fetchPagesAll(Long lastboardId,int size,BoardFilter filter){
        PageRequest pageRequest = PageRequest.of(0,size,Sort.by("createdAt").descending());
        return boardRepository.findBoardAll(lastboardId,filter,pageRequest);
    }

    @Async
    @Cacheable(value="getPosts",key="'getPosts'+ #categoryName +':'+ #lastboardId")
    public List<BoardResponse> getBoardByCategory(String categoryName,Long lastboardId,BoardFilter filter) { // 게시글 등록순으로 카테고리별로
            int pageSize = 10;

            Page<Board> boards = fetchPages(categoryName,lastboardId,pageSize,filter);

            if (boards.isEmpty())
                throw new RuntimeException("category or board is not exist.");

            List<BoardResponse> boardResponses = new ArrayList<>();
            boards.stream()
                .map(this::toBoardResponse)
                .forEach(boardResponses::add);

            return boardResponses;

    }

    private Page<Board> fetchPages(String categoryName,Long lastboardId,int size,BoardFilter filter){
        PageRequest pageRequest = PageRequest.of(0,size,Sort.by("createdAt").descending());
        return boardRepository.findBoardByCategory(categoryName,lastboardId,filter,pageRequest);
    }

    @Async
    @Cacheable(value="getPosts",key="'getPosts'+ ':'+'writer'+':'+#writer")
    public List<BoardResponse> searchBoardByUserNickname(String writer,Long lastboardId,BoardFilter filter) {
        int pageSize = 10;
        Page<Board> boards = fetchPagesByUserNickname(writer,lastboardId,pageSize,filter);

            if (boards.isEmpty())
                throw new RuntimeException("board not exist.");


            List<BoardResponse> boardResponses = new ArrayList<>();
            boards.stream()
                    .map(this::toBoardResponse)
                    .forEach(boardResponses::add);


            return boardResponses;
    }
    private Page<Board> fetchPagesByUserNickname(String writer,Long lastboardId,int size,BoardFilter filter){
        PageRequest pageRequest = PageRequest.of(0,size, Sort.by("createdAt").descending());
        return boardRepository.findByNickname(writer,lastboardId,filter,pageRequest);
    }

    @Async
    @Cacheable(value="getPosts",key="'getPosts'+':'+'keyword'+':'+#keyword")
    public List<BoardResponse> searchBoardByContent(String keyword,Long lastboardId,BoardFilter filter) {
        int pageSize = 10;
        Page<Board> boards = fetchPagesByContent(keyword,lastboardId,pageSize,filter);

            if (boards.isEmpty())
                throw new RuntimeException("board not exist.");


            List<BoardResponse> boardResponses = new ArrayList<>();
            boards.stream()
                .map(this::toBoardResponse)
                .forEach(boardResponses::add);
            return boardResponses;


    }
    private Page<Board> fetchPagesByContent(String keyword,Long lastboardId,int size,BoardFilter filter){
        PageRequest pageRequest = PageRequest.of(0,size,Sort.by("createdAt").descending());
        return boardRepository.findByContents(keyword,lastboardId,filter,pageRequest);
    }

    @Async
    @Cacheable(value="getPosts",key="'getPosts'+ ':'+'title'+':'+#title")
    public List<BoardResponse> searchBoardByTitle(String title, Long lastboardId, BoardFilter filter) {
        int pageSize = 10;
        Page<Board> boards = fetchPagesByTitle(title,lastboardId,pageSize,filter);

        if (boards.isEmpty())
            throw new RuntimeException("board not exist.");


        List<BoardResponse> boardResponses = new ArrayList<>();
        boards.stream()
                .map(this::toBoardResponse)
                .forEach(boardResponses::add);

        return boardResponses;
    }
    private Page<Board> fetchPagesByTitle(String title,Long lastboardId,int size,BoardFilter filter){

        PageRequest pageRequest = PageRequest.of(0,size, Sort.by("createdAt").descending());
        return boardRepository.findByTitle(title,lastboardId,filter,pageRequest);
    }

    @CacheEvict(value="getPosts", allEntries = true)
    public BoardResponse add(Long idx, postRequest request, List<MultipartFile> images) {
            String contents = request.getContents();
            if (contents == null || contents.equals(""))
                return null;

            Optional<Membership> optMember = userRepository.findById(idx);

            if (optMember.isPresent()) {
                Membership membership = optMember.get();

                Board board = new Board();
                board.setTitle(request.getTitle());

                Optional<Category> optCategory = categoryRepository.findById(request.getCategoryId());

                if(optCategory.isEmpty())
                    return null;

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

                return toBoardResponse(board);

            } else {
                throw new RuntimeException("Membership is not exist.");
            }
        }

    public BoardResponse setStatusSold(Long idx,Long boardId){
        if(validateBoard(idx,boardId)){
            Optional<Board> findBoard = this.boardRepository.findByBoardId(boardId);
            if (findBoard.isPresent()) {
                Board board = findBoard.get();
                board.setSortStatus(Board.SortStatus.SOLD);
                boardRepository.save(board);

                return toBoardResponse(board);

            }
        }
        return null;
    }

    public BoardResponse setStatusSale(Long idx,Long boardId){
        if(validateBoard(idx,boardId)){
            Optional<Board> findBoard = this.boardRepository.findByBoardId(boardId);
            if (findBoard.isPresent()) {
                Board board = findBoard.get();
                board.setSortStatus(Board.SortStatus.SALE);
                boardRepository.save(board);

                return toBoardResponse(board);

            }
        }
        return null;
    }

    @Transactional
    @CacheEvict(value="getPosts", allEntries = true)
    public BoardResponse updateBoard(Long boardId, postRequest request, List<MultipartFile> images) {

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
                        throw new RuntimeException("Invalid Category.");

                    board.setCategory(optCategory.get());
                }

                board.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
                board.setSortStatus(Board.SortStatus.SALE);

                Membership membership = board.getMembership();
                if(!membership.getCurProductRegion().equals(board.getCategory().getCategoryName()))
                    membership.setCurProductRegion(board.getCategory().getCategoryName());

                board.fillBoardImageUrl();
                boardRepository.save(board);

                return toBoardResponse(board);

            } else {
                throw new RuntimeException("Board is not exist.");
            }

    }

    @CacheEvict(value="getPosts", allEntries = true)
    public void deleteBoard(Long boardId) {
            Optional<Board> findBoard = boardRepository.findById(boardId);
            if (findBoard.isPresent()) {
                Board board = findBoard.get();

                deleteImagesByBoard(board);
                boardRepository.deleteByBoardId(boardId);
            } else {
                throw new RuntimeException("Board is not exist.");
            }

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
                throw new RuntimeException("이미지 저장에 실패하였습니다.", e);
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
                throw new RuntimeException("이미지 저장에 실패하였습니다.", e);
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