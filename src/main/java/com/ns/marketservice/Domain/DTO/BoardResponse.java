package com.ns.marketservice.Domain.DTO;

import com.ns.marketservice.Domain.Board;
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
public class BoardResponse {
    private Long boardId;
    private Long categoryId;
    private Board.SortStatus sortStatus;
    private String region;
    private String nickname;
    private String title;
    private String contents;
    private Integer hits;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    private List<String> boardImageUrl; //이미지

}