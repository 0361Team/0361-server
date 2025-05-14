package com._1.spring_rest_api.api.controller;

import com._1.spring_rest_api.api.dto.TextCreateRequest;
import com._1.spring_rest_api.api.dto.TextResponse;
import com._1.spring_rest_api.api.dto.TextUpdateRequest;
import com._1.spring_rest_api.service.ClaudeService;
import com._1.spring_rest_api.service.TextCommandService;
import com._1.spring_rest_api.service.TextQueryService;
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
import java.util.Map;

@RestController
@RequestMapping("/api/v1/texts")
@RequiredArgsConstructor
@Tag(name = "강의 텍스트 API", description = "강의 내용 텍스트 CRUD API")
public class TextController {

    private final TextQueryService textQueryService;
    private final TextCommandService textCommandService;
    private final ClaudeService claudeService;

    @PostMapping
    @Operation(
            summary = "강의 텍스트 생성",
            description = "클라이언트에서 전송한 강의 내용 텍스트를 저장합니다"
    )
    public ResponseEntity<Map<String, Long>> createText(
            @RequestBody @Valid TextCreateRequest request) {

        Long textId = textCommandService.createText(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(textId)
                .toUri();

        return ResponseEntity.created(location)
                .body(Map.of("id", textId));
    }

    @GetMapping("/{textId}")
    @Operation(
            summary = "강의 텍스트 조회",
            description = "ID를 통해 특정 강의 텍스트를 조회합니다"
    )
    public ResponseEntity<TextResponse> getTextById(
            @Parameter(description = "조회할 텍스트 ID")
            @PathVariable Long textId) {

        TextResponse response = textQueryService.getTextById(textId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/weeks/{weekId}")
    @Operation(
            summary = "주차별 강의 텍스트 목록 조회",
            description = "특정 주차에 포함된 모든 강의 텍스트를 조회합니다"
    )
    public ResponseEntity<List<TextResponse>> getTextsByWeekId(
            @Parameter(description = "조회할 주차 ID")
            @PathVariable Long weekId) {

        List<TextResponse> responses = textQueryService.getTextsByWeekId(weekId);
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{textId}")
    @Operation(
            summary = "강의 텍스트 수정",
            description = "특정 강의 텍스트의 내용이나 타입을 수정합니다"
    )
    public ResponseEntity<Void> updateText(
            @Parameter(description = "수정할 텍스트 ID")
            @PathVariable Long textId,
            @RequestBody @Valid TextUpdateRequest request) {

        textCommandService.updateText(textId, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{textId}")
    @Operation(
            summary = "강의 텍스트 삭제",
            description = "특정 강의 텍스트를 삭제합니다"
    )
    public ResponseEntity<Void> deleteText(
            @Parameter(description = "삭제할 텍스트 ID")
            @PathVariable Long textId) {

        textCommandService.deleteText(textId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/weeks/{weekId}")
    @Operation(
            summary = "주차별 모든 강의 텍스트 삭제",
            description = "특정 주차에 포함된 모든 강의 텍스트를 일괄 삭제합니다"
    )
    public ResponseEntity<Map<String, Integer>> deleteAllTextsByWeekId(
            @Parameter(description = "텍스트를 삭제할 주차 ID")
            @PathVariable Long weekId) {

        int deletedCount = textCommandService.deleteAllTextsByWeekId(weekId);
        return ResponseEntity.ok(Map.of("deletedCount", deletedCount));
    }

    @GetMapping("/summation/{textId}")
    @Operation(
            summary = "강의 요약 데이터 조회",
            description = "요약된 강의 정보를 제공합니다."
    )
    public ResponseEntity<?> getSummationById(
            @PathVariable Long textId
    ) {
        String summation = claudeService.generateSummation(textId);
        return ResponseEntity.ok(summation);
    }
}