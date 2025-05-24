package com._1.spring_rest_api.api.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/auth")
@Tag(name = "인증 API", description = "사용자 인증 관련 API")
public class AuthController {

}