package com._1.spring_rest_api.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/api/public/auth")
public class AuthController {

    @GetMapping("/kakao")
    public RedirectView kakaoLogin() {
        // Spring Security OAuth2 Client will handle the redirect to Kakao
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl("/oauth2/authorization/kakao");
        return redirectView;
    }
}