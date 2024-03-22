package com.ns.marketservice.Service;


import com.ns.marketservice.Domain.*;
import com.ns.marketservice.Domain.DTO.ReviewRequest;
import com.ns.marketservice.Domain.DTO.ReviewResponse;
import com.ns.marketservice.Repository.BoardRepository;
import com.ns.marketservice.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class ReviewService {

    private final com.ns.marketservice.Repository.ReviewRepository ReviewRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;


    public List<ReviewResponse> getReviews(Long boardId){

            Optional<Board> findBoard = this.boardRepository.findByBoardId(boardId);
            if (findBoard.isPresent()) {
                List<Review> Reviews =ReviewRepository.findAllByBoard_BoardId(boardId).orElse(null);

                List<ReviewResponse> ReviewResponsees = new ArrayList<>();
                for (Review review : Reviews) {
                    ReviewResponse ReviewResponse = new ReviewResponse();
                    ReviewResponse.setBoardId(review.getBoard().getBoardId());
                    ReviewResponse.setBody(review.getBody());
                    ReviewResponse.setNickname(review.getMembership().getNickname());
                    ReviewResponse.setId(review.getId());
                    ReviewResponse.setCreatedAt(review.getCreatedAt());
                    ReviewResponse.setUpdatedAt(review.getUpdatedAt());
                    ReviewResponsees.add(ReviewResponse);
                }

                return ReviewResponsees;
            } else {
                throw new RuntimeException("Invalid boardId.");
            }




    }

    public List<ReviewResponse> getMyReviewById(Long idx) {
            Optional<Membership> MemberOptional = userRepository.findById(idx);

            if (MemberOptional.isPresent()) {
                List<Review> myReviews = ReviewRepository.findAllByMembership_MembershipId(idx).orElse(null);

                if (myReviews.isEmpty())
                    throw new RuntimeException("MyReviews is Empty.");


                List<ReviewResponse> ReviewResponsees = new ArrayList<>();
                for (Review review : myReviews) {
                    ReviewResponse ReviewResponse = new ReviewResponse();
                    ReviewResponse.setBoardId(review.getBoard().getBoardId());
                    ReviewResponse.setBody(review.getBody());
                    ReviewResponse.setNickname(review.getMembership().getNickname());
                    ReviewResponse.setId(review.getId());
                    ReviewResponse.setCreatedAt(review.getCreatedAt());
                    ReviewResponse.setUpdatedAt(review.getUpdatedAt());
                    ReviewResponsees.add(ReviewResponse);
                }

                return ReviewResponsees;

            } else {
                throw new RuntimeException("Membership not exist.");
            }

    }


    public ReviewResponse addReview(Long boardId, ReviewRequest req, Long idx) {

        Optional<Membership> optMembership = userRepository.findById(idx);
        Optional<Board> optBoard = boardRepository.findByBoardId(boardId);

        if (optMembership.isPresent()) {
            Membership membership = optMembership.get();

            if (optBoard.isEmpty()) {
                throw new RuntimeException("Board not exist.");
            }

            Board board = optBoard.get();
            board.ReviewChange(board.getReviewCnt() + 1);
            Review review = new Review();
            review.setBoard(board);
            review.setMembership(membership);
            review.setBody(req.getBody());
            review.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            review.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            ReviewRepository.save(review);

            ReviewResponse ReviewResponse = new ReviewResponse();
            ReviewResponse.setId(review.getId());
            ReviewResponse.setBoardId(board.getBoardId());
            ReviewResponse.setBody(review.getBody());
            ReviewResponse.setNickname(review.getMembership().getNickname());
            ReviewResponse.setCreatedAt(review.getCreatedAt());
            ReviewResponse.setUpdatedAt(review.getUpdatedAt());

            return ReviewResponse;
        } else {
            throw new RuntimeException("Membership not exist.");
        }
    }

        @Transactional
        public ReviewResponse updateReview (Long reviewId, String newBody, Long idx){
            Optional<Review> findReview = ReviewRepository.findById(reviewId);
            if (findReview.isPresent()) {
                if (idx.equals(findReview.get().getMembership().getMembershipId())) {
                    Review review = findReview.get();
                    review.setBody(newBody);
                    review.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
                    ReviewRepository.save(review);

                    ReviewResponse ReviewResponse = new ReviewResponse();
                    ReviewResponse.setId(review.getId());
                    ReviewResponse.setBoardId(review.getBoard().getBoardId());
                    ReviewResponse.setBody(review.getBody());
                    ReviewResponse.setNickname(review.getMembership().getNickname());
                    ReviewResponse.setCreatedAt(review.getCreatedAt());
                    ReviewResponse.setUpdatedAt(review.getUpdatedAt());
                    return ReviewResponse;
                } else {
                    throw new RuntimeException("Invalid Membership Token.");
                }
            } else {
                throw new RuntimeException("Membership not exist.");
            }
        }

    public Long deleteReview(Long reviewId, Long idx) {
            Optional<Membership> findMembership = userRepository.findById(idx);
            Optional<Review> findReview = ReviewRepository.findById(reviewId);

            if (findMembership.isPresent()) {
                Membership Membership = findMembership.get();

                if (findReview.isEmpty())
                    throw new RuntimeException("Reviews not exist.");

                Membership writer = findReview.get().getMembership();
                Board board = findReview.get().getBoard();

                if (!Membership.equals(writer))
                    throw new RuntimeException("Invalid Membership Token.");

                // 댓글 삭제 하면 board 의 ReviewCnt 값도 1씩 줄어야 함.
                board.ReviewChange(board.getReviewCnt() - 1);

                ReviewRepository.delete(findReview.get());
                return board.getBoardId();
            } else {
                throw new RuntimeException("Membership not exist.");
            }

    }
}
