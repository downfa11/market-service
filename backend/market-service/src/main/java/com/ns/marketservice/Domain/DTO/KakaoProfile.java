package com.ns.marketservice.Domain.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
public class KakaoProfile {
    public Long id;
    public String connected_at;
    public KakaoProfile.Properties properties;
    public KakaoProfile.Kakao_Account kakao_account;

    @Data
    @JsonIgnoreProperties(ignoreUnknown=true)
    public class Properties {

        public String nickname;

    }
    @Data
    @JsonIgnoreProperties(ignoreUnknown=true)
    public class Kakao_Account {

        public Boolean profile_nickname_needs_agreement;
        public KakaoProfile.Kakao_Account.Profile profile;
        public Boolean has_email;
        public Boolean email_Needs_Agreement;
        public Boolean is_Email_Valid;
        public Boolean is_Email_Verified;
        public String email;

        @Data
        @JsonIgnoreProperties(ignoreUnknown=true)
        public class Profile {

            public String nickname;

        }

    }

}
