package com.ns.marketservice.Domain.DTO;

import com.ns.marketservice.Config.Numberic;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginMembershipRequest {

    @Numberic
    private Long membershipId;
}
