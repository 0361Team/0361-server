package com._1.spring_rest_api.api.controller;

import com._1.spring_rest_api.api.dto.CreateQuizRequest;
import com._1.spring_rest_api.api.dto.CreateQuizResponse;
import com._1.spring_rest_api.api.dto.QuizDetailResponse;
import com._1.spring_rest_api.api.dto.QuizSummaryResponse;
import com._1.spring_rest_api.service.QuizCommandService;
import com._1.spring_rest_api.service.QuizQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/quizzes")
@RequiredArgsConstructor
@Tag(name = "Quiz API", description = "퀴즈 생성 및 조회 API")
public class CustomQuizController {

    private final QuizCommandService quizCommandService;
    private final QuizQueryService quizQueryService;

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

    @GetMapping
    @Operation(summary = "사용자별 전체 퀴즈 목록 조회", description = "특정 사용자가 생성한 모든 퀴즈 목록을 조회합니다")
    public ResponseEntity<List<QuizSummaryResponse>> getUserQuizzes(
            @Parameter(description = "사용자 ID") @RequestParam Long userId) {
        List<QuizSummaryResponse> quizzes = quizQueryService.getQuizzesByUserId(userId);
        return ResponseEntity.ok(quizzes);
    }

    @GetMapping("/{quizId}")
    @Operation(summary = "퀴즈 상세 조회", description = "퀴즈 ID로 퀴즈의 상세 정보와 포함된 질문들을 조회합니다")
    public ResponseEntity<QuizDetailResponse> getQuizDetail(
            @Parameter(description = "퀴즈 ID") @PathVariable Long quizId) {
        QuizDetailResponse quiz = quizQueryService.getQuizDetail(quizId);
        return ResponseEntity.ok(quiz);
    }

    @DeleteMapping("/{quizId}")
    @Operation(summary = "퀴즈 삭제", description = "퀴즈 ID로 특정 퀴즈를 삭제합니다")
    public ResponseEntity<Void> deleteQuiz(
            @Parameter(description = "퀴즈 ID") @PathVariable Long quizId) {
        quizCommandService.deleteQuiz(quizId);
        return ResponseEntity.noContent().build();
    }
}
