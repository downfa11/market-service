package com.ns.marketservice.Domain.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MyDataMembership {
    private Long membershipId;

    private String name;
    private String nickname;
    private String address;
    private String email;
    private String region;
    private Integer exp;
    private Integer level;
    private String type;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    @Column(name = "created_at")
    private Timestamp createdAt;
}
