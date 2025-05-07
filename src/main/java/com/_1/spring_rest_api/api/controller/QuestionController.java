package com._1.spring_rest_api.api.controller;

import com._1.spring_rest_api.api.dto.FlashcardsRequest;
import com._1.spring_rest_api.api.dto.QuestionResponse;
import com._1.spring_rest_api.service.QuestionCommandService;
import com._1.spring_rest_api.service.QuestionQueryService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionQueryService questionQueryService;
    private final QuestionCommandService questionCommandService;

    @PostMapping("/weeks/{weekId}/flashcards")
    public ResponseEntity<Map<String, List<Long>>> saveFlashcards(
            @Parameter(description = "주차 ID") @PathVariable Long weekId,
            @RequestBody FlashcardsRequest request) {

        List<Long> questionIds = questionCommandService.saveFlashcards(weekId, request);

        Map<String, List<Long>> response = new HashMap<>();
        response.put("questionIds", questionIds);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .build()
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/weeks/{weekId}")
    public ResponseEntity<List<QuestionResponse>> getQuestionsByWeekId(
            @Parameter(description = "주차 ID") @PathVariable Long weekId) {

        List<QuestionResponse> questions = questionQueryService.getAllQuestionsByWeekId(weekId);
        return ResponseEntity.ok(questions);
    }

    @DeleteMapping("/{questionId}")
    public ResponseEntity<Void> deleteQuestion(
            @Parameter(description = "질문 ID") @PathVariable Long questionId) {

        questionCommandService.deleteQuestion(questionId);
        return ResponseEntity.noContent().build();
    }
}
