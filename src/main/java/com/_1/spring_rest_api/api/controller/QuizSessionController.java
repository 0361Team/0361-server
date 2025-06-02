package com._1.spring_rest_api.api.controller;

import com._1.spring_rest_api.api.dto.*;
import com._1.spring_rest_api.service.QuizSessionCommandService;
import com._1.spring_rest_api.service.QuizSessionQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
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

    @DeleteMapping("/batch")
    @Operation(summary = "여러 퀴즈 세션 배치 삭제 (JWT 토큰 기반)",
            description = """
                    여러 퀴즈 세션을 한 번에 삭제합니다. 
                    - JWT 토큰으로 현재 사용자를 인증합니다
                    - 본인이 소유한 세션만 삭제 가능합니다
                    - 최대 100개까지 처리 가능합니다
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "배치 삭제 완료 (일부 실패 포함 가능)"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (세션 ID 목록이 비어있거나 너무 많음)"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자 (JWT 토큰 없음 또는 유효하지 않음)"),
    })
    public ResponseEntity<DeleteSessionsResponse> deleteSessions(
            @Valid @RequestBody DeleteSessionsRequest request) {

        try {
            DeleteSessionsResponse response = quizSessionCommandService.deleteSessions(request);
            return ResponseEntity.ok(response);

        } catch (AccessDeniedException e) {
            // JWT 토큰이 없거나 유효하지 않은 경우
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        }
    }
}