package com.ns.marketservice.Domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name ="board")
@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="board_id",nullable = false)
    private Long boardId;

    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    private Membership membership;

    public enum SortStatus{
        SALE,SOLD
    }
    private SortStatus sortStatus;

    private String region;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String title;

    private String contents;
    private Long price;
    private Integer hits; //조회수

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    @Column(name = "created_at")
    private Timestamp createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private final List<BoardImage> boardImage = new ArrayList<>();

    @Transient
    private List<String> boardImageUrl;

    @OneToMany(mappedBy = "board", orphanRemoval = true)
    private List<Review> review;
    private Integer reviewCnt;

    @PostLoad
    public void fillBoardImageUrl() {
        this.boardImageUrl = new ArrayList<>();
        for (BoardImage image : this.boardImage) {
            this.boardImageUrl.add(image.getUrl());
        }
    }

    public void ReviewChange(Integer reviewCnt) {
        this.reviewCnt = reviewCnt;
    }

    public Integer getReviewCnt() {

        return reviewCnt != null ? reviewCnt : 0;
    }
}
