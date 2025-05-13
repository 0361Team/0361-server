package com._1.spring_rest_api.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/api/public/auth")
@Tag(name = "인증 API", description = "사용자 인증 관련 API")
public class AuthController {

    @GetMapping("/kakao")
    @Operation(
            summary = "카카오 OAuth2 로그인",
            description = "카카오 OAuth2 인증 페이지로 리다이렉트합니다. 사용자가 카카오 계정으로 로그인하면 설정된 콜백 URL로 인증 코드와 함께 리다이렉트됩니다."
    )
    @SecurityRequirements
    public RedirectView kakaoLogin() {
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl("/oauth2/authorization/kakao");
        return redirectView;
    }
}