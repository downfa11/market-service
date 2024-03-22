package com.ns.marketservice.Domain.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class postRequest {

    private Long categoryId;
    private String region;
    private String title;
    private String contents;
    private Long price;


}
