package com._1.spring_rest_api.api.controller;

import com._1.spring_rest_api.api.dto.GenerateQuestionsRequest;
import com._1.spring_rest_api.api.dto.QuestionResponse;
import com._1.spring_rest_api.service.QuestionCommandService;
import com._1.spring_rest_api.service.QuestionQueryService;
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
@RequestMapping("/api/v1/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionQueryService questionQueryService;
    private final QuestionCommandService questionCommandService;

    @PostMapping("/weeks/{weekId}/generate")
    @Operation(
            summary = "AI 질문 생성 및 저장",
            description = "AI를 사용하여 주차 내용을 기반으로 질문을 자동 생성하고 저장합니다"
    )
    public ResponseEntity<Map<String, List<Long>>> generateQuestions(
            @Parameter(description = "주차 ID") @PathVariable Long weekId,
            @RequestBody @Valid GenerateQuestionsRequest request) {

        List<Long> questionIds = questionCommandService.generateAndSaveQuestions(
                weekId, request.getMinQuestionCount());

        Map<String, List<Long>> response = new HashMap<>();
        response.put("questionIds", questionIds);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/weeks/{weekId}")
    @Operation(
            summary = "주차별 질문 조회",
            description = "특정 주차에 속한 모든 질문을 조회합니다"
    )
    public ResponseEntity<List<QuestionResponse>> getQuestionsByWeekId(
            @Parameter(description = "주차 ID") @PathVariable Long weekId) {

        List<QuestionResponse> questions = questionQueryService.getAllQuestionsByWeekId(weekId);
        return ResponseEntity.ok(questions);
    }

    @DeleteMapping("/{questionId}")
    @Operation(
            summary = "질문 삭제",
            description = "ID로 지정된 특정 질문을 삭제합니다"
    )
    public ResponseEntity<Void> deleteQuestion(
            @Parameter(description = "질문 ID") @PathVariable Long questionId) {

        questionCommandService.deleteQuestion(questionId);
        return ResponseEntity.noContent().build();
    }
}
