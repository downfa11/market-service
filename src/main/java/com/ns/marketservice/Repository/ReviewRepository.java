package com.ns.marketservice.Repository;

import com.ns.marketservice.Domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<List<Review>> findAllByBoard_BoardId(Long boardId);
    Optional<List<Review>> findAllByMembership_MembershipId(Long MembershipId);

}
