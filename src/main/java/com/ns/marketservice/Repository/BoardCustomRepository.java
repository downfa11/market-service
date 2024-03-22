package com.ns.marketservice.Repository;


import com.ns.marketservice.Domain.Board;
import com.ns.marketservice.Domain.DTO.BoardFilter;
import com.ns.marketservice.Domain.Membership;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


public interface BoardCustomRepository {


    Page<Board> findByNickname(String nickName, Long lastboardId, BoardFilter filter, Pageable pageable);
    Page<Board> findBoardByCategory(String categoryName, Long lastboardId, BoardFilter filter, Pageable pageable);

    Page<Board> findBoardAll(Long lastBoardId, BoardFilter filter, Pageable pageable);
    Page<Board> findByTitle(String title, Long lastboardId, BoardFilter filter,Pageable pageable);
    Page<Board> findByContents(String keyword, Long lastboardId,BoardFilter filter, Pageable pageable);

}
