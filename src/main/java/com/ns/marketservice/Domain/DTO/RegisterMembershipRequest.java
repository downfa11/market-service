package com.ns.marketservice.Domain.DTO;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@EqualsAndHashCode()
@AllArgsConstructor
@NoArgsConstructor
public class RegisterMembershipRequest {

    @NotNull
    private String region;

    @AssertTrue
    private boolean isValid;
}