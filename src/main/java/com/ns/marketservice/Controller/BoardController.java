package com.ns.marketservice.Controller;

import com.ns.marketservice.Domain.Board;
import com.ns.marketservice.Domain.DTO.BoardFilter;
import com.ns.marketservice.Domain.DTO.BoardResponse;
import com.ns.marketservice.Domain.DTO.messageEntity;
import com.ns.marketservice.Domain.DTO.postRequest;
import com.ns.marketservice.Service.BoardService;
import com.ns.marketservice.Utils.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@RestController
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {


    private final BoardService boardService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/add")
    public ResponseEntity<messageEntity> add(@RequestBody postRequest request, @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        Long idx = jwtTokenProvider.getMembershipIdbyToken();
        if (idx == 0)
            return ResponseEntity.ok().body(new messageEntity("Fail","Not Authorization or boardId is incorrect."));

        if (images != null && images.size() > 3)
            return ResponseEntity.ok().body(new messageEntity("Fail","Image max size 3."));

        return ResponseEntity.ok()
                .body(new messageEntity("Success",boardService.add(idx,request, images)));
    }

    @PostMapping("/add/temp")
    public ResponseEntity<messageEntity> addTemp(@RequestParam("memberId") Long idx) {

        Random random = new Random();

        postRequest temp = new postRequest();
        temp.setCategoryId((long)random.nextInt(4));
        temp.setTitle("test title: " + random.nextInt(1000));
        temp.setContents("test contents: " + random.nextInt(1000));
        temp.setPrice((long) random.nextInt(10000));

        return ResponseEntity.ok()
                .body(new messageEntity("Success",boardService.add(idx,temp, null)));
    }

    @PatchMapping("/update")
    public ResponseEntity<messageEntity> updateBoard(@RequestParam("boardId") Long boardId, @RequestBody postRequest request,
                                              @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        Long idx = jwtTokenProvider.getMembershipIdbyToken();
        if (idx == 0 || !boardService.validateBoard(idx,boardId))
            return ResponseEntity.ok().body(new messageEntity("Fail","Not Authorization or boardId is incorrect."));

        if (images != null && images.size() > 3)
            return ResponseEntity.ok().body(new messageEntity("Fail","Image max size 3."));

        return ResponseEntity.ok()
                .body(new messageEntity("Success",boardService.updateBoard(boardId, request, images)));

    }

    @DeleteMapping("/delete")
    public ResponseEntity<messageEntity> deleteBoard(@RequestParam("boardId") Long boardId) {
        Long idx = jwtTokenProvider.getMembershipIdbyToken();
        if (idx == 0 || !boardService.validateBoard(idx,boardId))
            return ResponseEntity.ok().body(new messageEntity("Fail","Not Authorization or request is incorrect."));

        boardService.deleteBoard(boardId);

        return ResponseEntity.ok()
                .body(new messageEntity("Success",boardId));
    }

    //저장된 이미지 조회
    //이미지 네임을 알 수 있음 그 거를 기반으로 이미지 조회하기
    //http://localhost:8080/board/images/7f658d91-ef68-4b59-a381-af5bc9938768_fighting.png
    @ResponseBody
    @GetMapping("/images/{imageName}")
    public ResponseEntity<byte[]> getReviewImage(@PathVariable String imageName) {
        String projectPath = System.getProperty("user.dir") + "\\src\\main\\resources\\templates\\image\\";
        String imagePath = projectPath + imageName;

        try {
            FileInputStream imageStream = new FileInputStream(imagePath);
            byte[] imageBytes = imageStream.readAllBytes();
            imageStream.close();

            String contentType = determineContentType(imageName); // 이미지 파일 확장자에 따라 MIME 타입 결정

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));

            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String determineContentType(String imageName) {
        String extension = FilenameUtils.getExtension(imageName);
        switch (extension.toLowerCase()) {
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            default:
                return "application/octet-stream";
        }
    }

    @GetMapping("/list")
    public ResponseEntity<messageEntity> getBoards() {
            List<BoardResponse> boards = boardService.getBoards();
            List<BoardResponse> dayboard = new ArrayList<>();
            Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());

            for (BoardResponse board : boards) {
                Timestamp boardTimestamp = board.getCreatedAt();
                long timeDifference = currentTimestamp.getTime() - boardTimestamp.getTime();
                long hoursDifference = TimeUnit.MILLISECONDS.toMinutes(timeDifference); // toMinutes->toHours

                if (hoursDifference <= 1) //1 -> 24
                    dayboard.add(board);

            }

        return ResponseEntity.ok()
                .body(new messageEntity("Success",dayboard));
    }
    @GetMapping("/list/{CategoryName}/{offset}")
    public ResponseEntity<messageEntity> getBoardsByCategory(@PathVariable String CategoryName, @PathVariable Long offset, @RequestBody BoardFilter filter) {
        return ResponseEntity.ok()
                .body(new messageEntity("Success",boardService.getBoardByCategory(CategoryName,offset,filter)));
    }

    @GetMapping("/list/{offset}")
    public ResponseEntity<messageEntity> getBoardsAll(@PathVariable Long offset, @RequestBody BoardFilter filter) {

        return ResponseEntity.ok()
                .body(new messageEntity("Success",boardService.getBoardsAll(offset,filter)));
    }


    @GetMapping("/myboard")
    public ResponseEntity<messageEntity> getMyBoards () {
        Long idx = jwtTokenProvider.getMembershipIdbyToken();
        if (idx == 0)
            return ResponseEntity.ok().body(new messageEntity("Fail","Not Authorization or request is incorrect."));

        return ResponseEntity.ok()
                .body(new messageEntity("Success",boardService.getMyBoards(idx)));
    }

    @GetMapping("/board")
    public ResponseEntity<messageEntity> getMembershipBoards (@RequestParam("memberId") Long idx) {
        return ResponseEntity.ok()
                .body(new messageEntity("Success",boardService.getMyBoards(idx)));

    }
    @GetMapping("/{boardId}")
    public ResponseEntity<messageEntity> getBoardByBoardId (@PathVariable Long boardId){
        Long idx = jwtTokenProvider.getMembershipIdbyToken();
        if (idx == 0)
            return ResponseEntity.ok().body(new messageEntity("Fail","Not Authorization or request is incorrect."));

        return ResponseEntity.ok()
                .body(new messageEntity("Success",boardService.getBoardByBoardId(boardId,idx)));

    }
    @GetMapping("/search/user/{offset}")
    public ResponseEntity<messageEntity> getBoardByUserId (@RequestParam("nickname") String nickname, @PathVariable Long offset, @RequestBody BoardFilter filter){
        if (nickname == null)
            return ResponseEntity.ok().body(new messageEntity("Fail","Search Keyword 'nickname' is null."));

        return ResponseEntity.ok()
                .body(new messageEntity("Success",boardService.searchBoardByUserNickname(nickname,offset,filter)));

    }

    @GetMapping("/search/content/{offset}")
    public ResponseEntity<messageEntity> searchBoardByContent (@RequestParam("content") String content, @PathVariable Long offset, @RequestBody BoardFilter filter){
        if (content == null)
            return ResponseEntity.ok().body(new messageEntity("Fail","Search Keyword 'content' is null."));

        return ResponseEntity.ok()
                .body(new messageEntity("Success",boardService.searchBoardByContent(content,offset,filter)));

    }

    @GetMapping("/search/title/{offset}")
    public ResponseEntity<messageEntity> searchBoardByTitle (@RequestParam("title") String title, @PathVariable Long offset, @RequestBody BoardFilter filter){
         if (title == null)
            return ResponseEntity.ok().body(new messageEntity("Fail","Search Keyword 'title' is null."));

        return ResponseEntity.ok()
                .body(new messageEntity("Success",boardService.searchBoardByTitle(title,offset,filter)));


    }

    @PostMapping("setStatus/sold")
    public ResponseEntity<messageEntity> setBoardStatusSold(@RequestParam("boardId") Long boardId){
        Long idx = jwtTokenProvider.getMembershipIdbyToken();
        if (idx == 0)
            return ResponseEntity.ok().body(new messageEntity("Fail","Not Authorization or request is incorrect."));

        return ResponseEntity.ok()
                .body(new messageEntity("Success",boardService.setStatusSold(idx,boardId)));



    }

    @PostMapping("setStatus/sale")
    public ResponseEntity<messageEntity> setBoardStatusSale(@RequestParam("boardId") Long boardId){
        Long idx = jwtTokenProvider.getMembershipIdbyToken();

        if (idx == 0)
            return ResponseEntity.ok().body(new messageEntity("Fail","Not Authorization or request is incorrect."));

        return ResponseEntity.ok()
                .body(new messageEntity("Success",boardService.setStatusSale(idx,boardId)));

    }
}
