package com._1.spring_rest_api.api.controller;

import com._1.spring_rest_api.api.dto.*;
import com._1.spring_rest_api.service.QuizSessionCommandService;
import com._1.spring_rest_api.service.QuizSessionQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/quiz-sessions")
@RequiredArgsConstructor
@Tag(name = "Quiz Session API", description = "퀴즈 세션 관리 API")
public class QuizSessionController {

    private final QuizSessionCommandService quizSessionCommandService;
    private final QuizSessionQueryService quizSessionQueryService;

    @GetMapping("/user/{userId}")
    @Operation(summary = "사용자별 퀴즈 세션 목록 조회", description = "특정 사용자의 모든 퀴즈 세션 목록을 조회합니다")
    public ResponseEntity<List<QuizSessionResponse>> getUserQuizSessions(
            @Parameter(description = "사용자 ID") @PathVariable Long userId) {
        List<QuizSessionResponse> sessions = quizSessionQueryService.getUserQuizSessions(userId);
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/{sessionId}")
    @Operation(summary = "퀴즈 세션 상세 조회", description = "퀴즈 세션의 상세 정보를 조회합니다")
    public ResponseEntity<QuizSessionDetailResponse> getSessionDetail(
            @Parameter(description = "세션 ID") @PathVariable Long sessionId) {
        QuizSessionDetailResponse session = quizSessionQueryService.getSessionDetail(sessionId);
        return ResponseEntity.ok(session);
    }

    @PostMapping("/{sessionId}/answer")
    @Operation(summary = "질문 답변", description = "현재 질문에 대한 답변을 제출하고 결과를 반환합니다")
    public ResponseEntity<AnswerResponse> answerQuestion(
            @Parameter(description = "세션 ID") @PathVariable Long sessionId,
            @Valid @RequestBody AnswerRequest request) {
        AnswerResponse response = quizSessionCommandService.answerQuestion(sessionId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{sessionId}/complete")
    @Operation(summary = "퀴즈 세션 완료", description = "퀴즈 세션을 완료 처리합니다")
    public ResponseEntity<Map<String, Object>> completeSession(
            @Parameter(description = "세션 ID") @PathVariable Long sessionId) {
        quizSessionCommandService.completeSession(sessionId);

        // 성공 응답 생성
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Quiz session completed successfully");
        response.put("sessionId", sessionId);

        return ResponseEntity.ok(response);
    }
}