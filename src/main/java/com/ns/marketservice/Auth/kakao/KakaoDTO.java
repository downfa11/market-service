package com.ns.marketservice.Auth.kakao;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Builder
@Data
public class KakaoDTO {

    private long id;
    private String email;
    private String nickname;

}