package com.ns.marketservice.Auth.naver;

import com.ns.marketservice.Domain.DTO.messageEntity;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("naver")
public class NaverController {

    private final NaverService naverService;

    @GetMapping("/callback")
    public ResponseEntity<messageEntity> callback(HttpServletRequest request) throws Exception {
        NaverDTO naverInfo = naverService.getNaverInfo(request.getParameter("code"));

        return ResponseEntity.ok()
                .body(new messageEntity("Success", naverInfo));
    }

}