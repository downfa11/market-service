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


    List<Board> findByNickname(String nickName,Long offset,BoardFilter filter);
    List<Board> findBoardByCategory(String categoryName, Long offset, BoardFilter filter);

    List<Board> findBoardAll(Long offset,BoardFilter filter);
    List<Board> findByTitle(String title, Long offset, BoardFilter filter);
    List<Board> findByContents(String keyword,  Long offset,BoardFilter filter);

}
