package com.ns.marketservice.Domain.DTO;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode()
@AllArgsConstructor
@NoArgsConstructor
public class RegisterMembershipRequest {
    @NotNull
    private String name;
    @NotNull
    private  String nickname;

    @NotNull
    private String email;
    @NotNull
    @NotBlank
    private String address;
    @NotNull
    private String region;

    @AssertTrue
    private boolean isValid;
}