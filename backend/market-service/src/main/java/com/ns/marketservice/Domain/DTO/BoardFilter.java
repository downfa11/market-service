package com.ns.marketservice.Domain.DTO;

import com.ns.marketservice.Domain.Board;
import com.ns.marketservice.Domain.Category;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class BoardFilter {
    private List<Board.SortStatus> sortStatus;
    private List<String> regions;
    private List<Category> categories;
}
