package com.ns.marketservice.Domain.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {
    private Long id;
    private Long boardId;
    private String nickname;
    private String body;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}