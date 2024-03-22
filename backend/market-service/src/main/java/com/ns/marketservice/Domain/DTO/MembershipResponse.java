package com.ns.marketservice.Domain.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MembershipResponse {
    private Long id;
    private String jwt;
}
