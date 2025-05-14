package com._1.spring_rest_api.api.controller;

import com._1.spring_rest_api.api.dto.CreateQuizRequest;
import com._1.spring_rest_api.api.dto.CreateQuizResponse;
import com._1.spring_rest_api.service.QuizCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/quizzes")
@RequiredArgsConstructor
@Tag(name = "Quiz API", description = "퀴즈 생성 및 조회 API")
public class CustomQuizController {

    private final QuizCommandService quizCommandService;

    @PostMapping
    @Operation(summary = "퀴즈 생성", description = "사용자가 선택한 주차들로 새로운 퀴즈를 생성합니다")
    public ResponseEntity<CreateQuizResponse> createQuiz(@Valid @RequestBody CreateQuizRequest request) {
        Long quizId = quizCommandService.createQuiz(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(quizId)
                .toUri();

        return ResponseEntity.created(location).body(new CreateQuizResponse(quizId));
    }
}
