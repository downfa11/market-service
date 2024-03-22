package com.ns.marketservice.Domain;

import com.fasterxml.jackson.annotation.JsonFormat;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;


@Entity
@Table(name ="membership")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Membership {

    @Id
    @GeneratedValue
    @Column(name="id")
    private Long membershipId;

    private String name;
    private String nickname;
    private String address;
    private String email;
    private Integer role; // 0 : user, 1 : manager, 2 : admin
    private String region;
    private Integer exp;
    private Integer level;
    private String type;

    private String curProductRegion;

    private boolean isValid;

    private String refreshToken;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    @Column(name = "created_at")
    private Timestamp createdAt;

}
