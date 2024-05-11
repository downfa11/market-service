package com.ns.marketservice.Domain.DTO;

import com.ns.marketservice.Config.Numberic;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequest {
    @Numberic
    private String body;

}
