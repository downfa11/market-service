package com.ns.marketservice.Auth.kakao;

import com.ns.marketservice.Domain.DTO.messageEntity;
import com.ns.marketservice.Service.UserService;
import com.ns.marketservice.Utils.jwtToken;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("kakao")
public class KakaoController {

    private final KakaoService kakaoService;
    private final UserService userService;

    @RequestMapping(value="/", method= RequestMethod.GET)
    public String login(Model model) {
        model.addAttribute("kakaoUrl", kakaoService.getKakaoLogin());
        //model.addAttribute("naverUrl", naverService.getNaverLogin());
        return "index";
    }

    @GetMapping("/login")
    public ResponseEntity<messageEntity> login(){
        return ResponseEntity.ok().body(new messageEntity("Success",kakaoService.getKakaoLogin()));
    }

    @GetMapping("/callback")
    public ResponseEntity<messageEntity> callback(HttpServletRequest request) throws Exception {
        KakaoDTO kakaoInfo = kakaoService.getKakaoInfo(request.getParameter("code"));


        Long email = kakaoInfo.getId();
        jwtToken token = userService.LoginMembership(email.toString());
        // token이 null이면 회원가입을 진행해야함. /membership/register
        // email(계정아이디)에 nickname을 넣고, password에 id를 넣는다.
        // 회원가입 후 다시 callback으로 이동하게 된다면, 토큰을 발급해준다.

        // token이 발급되었다면 로그인이 된거다.
        return ResponseEntity.ok()
                .body(new messageEntity("Success "+token.toString(), kakaoInfo));
    }



}
