package com.ns.marketservice.Controller;

import com.ns.marketservice.Domain.DTO.ReviewRequest;
import com.ns.marketservice.Domain.DTO.ReviewResponse;
import com.ns.marketservice.Domain.DTO.messageEntity;
import com.ns.marketservice.Service.ReviewService;
import com.ns.marketservice.Utils.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/review")
public class ReviewController {
    private final ReviewService reviewService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/boardId={boardId}")
    public ResponseEntity<messageEntity> getCommentByBoardId(@PathVariable Long boardId) {
        if(boardId == null)
            return ResponseEntity.ok().body(new messageEntity("Fail","boardId is null."));

        return ResponseEntity.ok()
                .body(new messageEntity("Success",reviewService.getReviews(boardId)));
    }

    @GetMapping("/myreview")
    public ResponseEntity<messageEntity> getMyReviewsById() {

        Long idx = jwtTokenProvider.getMembershipIdbyToken();
        if (idx == 0)
            return ResponseEntity.ok()
                    .body(new messageEntity("Fail","Not Authorization."));


        return ResponseEntity.ok()
                .body(new messageEntity("Success",reviewService.getMyReviewById(idx)));
    }

    @PostMapping("/add")
    public ResponseEntity<messageEntity> addComments(@RequestParam("boardId") Long boardId, @RequestBody ReviewRequest req) {
            Long idx = jwtTokenProvider.getMembershipIdbyToken();
            if (idx == 0 || req == null)
                return ResponseEntity.ok().body(new messageEntity("Fail","Not Authorization or request is incorrect."));

        return ResponseEntity.ok().body(new messageEntity("Success",reviewService.addReview(boardId, req, idx)));
    }

    @PatchMapping("/update")
    public ResponseEntity<messageEntity> updateComment(@RequestParam("reviewId") Long reviewId,@RequestBody ReviewRequest request) {
            Long idx = jwtTokenProvider.getMembershipIdbyToken();
            if (idx == 0)
                return ResponseEntity.ok()
                        .body(new messageEntity("Fail","Not Authorization or request is incorrect."));

        return ResponseEntity.ok()
                .body(new messageEntity("Success",reviewService.updateReview(reviewId, request.getBody(), idx)));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<messageEntity> deleteComment(@RequestParam("reviewId") Long reviewId) {
            Long idx = jwtTokenProvider.getMembershipIdbyToken();
        if (idx == 0)
            return ResponseEntity.ok()
                    .body(new messageEntity("Fail","Not Authorization or request is incorrect."));

        return ResponseEntity.ok()
                .body(new messageEntity("Success",reviewService.deleteReview(reviewId, idx)));
    }

}
