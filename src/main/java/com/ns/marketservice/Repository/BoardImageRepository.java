package com.ns.marketservice.Repository;

import com.ns.marketservice.Domain.Board;
import com.ns.marketservice.Domain.BoardImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardImageRepository extends JpaRepository<BoardImage, Long> {
    List<BoardImage> findByBoard (Board board);
}