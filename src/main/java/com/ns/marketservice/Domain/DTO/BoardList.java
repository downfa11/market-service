package com.ns.marketservice.Domain.DTO;

import com.ns.marketservice.Domain.Board;
import com.ns.marketservice.Domain.Membership;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoardList {
    private Long boardId;
    private Long categoryId;
    private Board.SortStatus sortStatus;
    private String region;
    private String nickname;
    private String title;
    private Integer hits;
    private Timestamp createdAt;


}