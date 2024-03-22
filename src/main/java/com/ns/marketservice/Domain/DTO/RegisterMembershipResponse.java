package com.ns.marketservice.Domain.DTO;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode()
public class RegisterMembershipResponse {
    @NotNull
    private Long membershipId;

    @NotNull
    private String name;
    @NotNull
    private String nickname;

    @NotNull
    private String email;
    @NotNull
    @NotBlank
    private String address;


    @NotNull
    private String region;

}
