package com.ns.marketservice.Controller;

import com.ns.marketservice.Auth.kakao.KakaoDTO;
import com.ns.marketservice.Auth.kakao.KakaoService;
import com.ns.marketservice.Domain.DTO.*;
import com.ns.marketservice.Domain.Membership;
import com.ns.marketservice.Utils.jwtToken;
import com.ns.marketservice.Service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Arrays;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/membership")
public class UserController {

    private final UserService userService;
    private final KakaoService kakaoService;

    @PostMapping(path="/oauth/register")
    public ResponseEntity<messageEntity> registerMembership(HttpServletRequest httpRequest,@RequestBody RegisterMembershipRequest request) throws Exception {
        KakaoDTO kakaoInfo = kakaoService.getKakaoInfo(httpRequest.getParameter("code"));
        Long id = kakaoInfo.getId();
        String nickname = kakaoInfo.getNickname();
        String email = kakaoInfo.getEmail();
        if(kakaoInfo!=null)
            return ResponseEntity.ok().body(new messageEntity("Success",userService.RegisterMembership(id,email,nickname,request)));
        else
            return ResponseEntity.ok().body(new messageEntity("Fail","Incorrect OAuth2 autorization."));

    }

    @Transactional
    @PostMapping(path="/login-temp")
    public ResponseEntity<messageEntity> loginMembershipTemp(@RequestBody LoginMembershipRequest request,HttpServletRequest httprequest,HttpServletResponse httpresponse){
        if(request.getMembershipId()==null)
            return ResponseEntity.ok().body(new messageEntity("Fail","Not Authorization or request is incorrect."));

        //WaitingQueueConnect("default", request.getMembershipId(),httprequest,httpresponse);
        return ResponseEntity.ok().body(new messageEntity("Success",userService.LoginMembershipTemp(request)));
    }


    @GetMapping(value = "/mypage")
    public ResponseEntity<messageEntity> myDataMembershipShow(){
        return ResponseEntity.ok().body(new messageEntity("Success",userService.myDataMembershipShow()));
    }

    @DeleteMapping("/users") // 사용자접근이 아닌 관리자용
    public ResponseEntity<messageEntity> deleteMembership(Long memberId){
        if(memberId==null)
            return ResponseEntity.ok().body(new messageEntity("Fail","Not Authorization or request is incorrect."));

        return ResponseEntity.ok().body(new messageEntity("Success",userService.deleteByMembership(memberId)));
    }


    @PostMapping(path="/refresh-token")
    ResponseEntity<messageEntity> refreshToken(@RequestBody RefreshTokenRequest request){
        if(request.getRefreshToken()==null)
            return ResponseEntity.ok().body(new messageEntity("Fail","RefreshToken is incorrect."));

        return ResponseEntity.ok()
                .body(new messageEntity("Success",userService.refreshJwtToken(request)));
    }

    @PostMapping(path="/token-validate")
    ResponseEntity<messageEntity> validateToken(@RequestBody ValidateTokenRequest request){
        if(request.getJwtToken()==null)
                return ResponseEntity.ok().body(new messageEntity("Fail","Token is incorrect."));

        return ResponseEntity.ok().body(new messageEntity("Success",userService.validateJwtToken(request)));
    }

    @PostMapping(path="/token-membership")
    ResponseEntity<messageEntity> getMembershipByJwtToken(@RequestBody ValidateTokenRequest request){
        if(request.getJwtToken()==null)
            return ResponseEntity.ok().body(new messageEntity("Fail","Token is incorrect."));

        return ResponseEntity.ok().body(new messageEntity("Success",userService.getMembershipByJwtToken(request)));
    }
}
