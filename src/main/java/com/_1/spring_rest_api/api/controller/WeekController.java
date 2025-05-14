package com._1.spring_rest_api.api.controller;

import com._1.spring_rest_api.api.dto.WeekRequest;
import com._1.spring_rest_api.api.dto.WeekResponse;
import com._1.spring_rest_api.entity.Question;
import com._1.spring_rest_api.entity.QuizWeekMapping;
import com._1.spring_rest_api.entity.SupplementalFile;
import com._1.spring_rest_api.entity.Text;
import com._1.spring_rest_api.service.WeekService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/weeks")
@RequiredArgsConstructor
@Tag(name = "Week", description = "Week 관리 API")
public class WeekController {

    private final WeekService weekService;

    @Operation(summary = "모든 Week 목록 조회", description = "시스템에 등록된 모든 Week를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = WeekResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<WeekResponse>> getAllWeeks() {
        return ResponseEntity.ok(weekService.getAllWeeks());
    }

    @Operation(summary = "Course ID로 Week 목록 조회", description = "특정 코스에 속한 모든 Week를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "코스를 찾을 수 없음")
    })
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<WeekResponse>> getWeeksByCourseId(@PathVariable Long courseId) {
        return ResponseEntity.ok(weekService.getWeeksByCourseId(courseId));
    }

    @Operation(summary = "Week ID로 특정 Week 조회", description = "ID로 특정 Week를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "Week를 찾을 수 없음")
    })
    @GetMapping("/{weekId}")
    public ResponseEntity<WeekResponse> getWeekById(@PathVariable Long weekId) {
        return ResponseEntity.ok(weekService.getWeekById(weekId));
    }

    @Operation(summary = "새로운 Week 생성", description = "새로운 Week를 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "코스를 찾을 수 없음")
    })
    @PostMapping
    public ResponseEntity<WeekResponse> createWeek(@RequestBody WeekRequest request) {
        WeekResponse response = weekService.createWeek(
                request.getCourseId(),
                request.getTitle(),
                request.getWeekNumber()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Week 정보 업데이트", description = "기존 Week의 정보를 업데이트합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "업데이트 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "Week를 찾을 수 없음")
    })
    @PutMapping("/{weekId}")
    public ResponseEntity<WeekResponse> updateWeek(
            @PathVariable Long weekId,
            @RequestBody WeekRequest request) {
        WeekResponse response = weekService.updateWeek(
                weekId,
                request.getTitle(),
                request.getWeekNumber()
        );
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Week 삭제", description = "특정 Week를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "Week를 찾을 수 없음")
    })
    @DeleteMapping("/{weekId}")
    public ResponseEntity<Void> deleteWeek(@PathVariable Long weekId) {
        weekService.deleteWeek(weekId);
        return ResponseEntity.noContent().build();
    }
/*
    @Operation(summary = "Week에 Text 추가", description = "특정 Week에 Text를 추가합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "추가 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "Week를 찾을 수 없음")
    })
    @PostMapping("/{weekId}/texts")
    public ResponseEntity<WeekResponse> addTextToWeek(
            @PathVariable Long weekId,
            @RequestBody Text text) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(weekService.addTextToWeek(weekId, text));
    }

    @Operation(summary = "Week에서 Text 제거", description = "특정 Week에서 Text를 제거합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "제거 성공"),
            @ApiResponse(responseCode = "404", description = "Week 또는 Text를 찾을 수 없음")
    })
    @DeleteMapping("/{weekId}/texts/{textId}")
    public ResponseEntity<WeekResponse> removeTextFromWeek(
            @PathVariable Long weekId,
            @PathVariable Long textId) {
        return ResponseEntity.ok(weekService.removeTextFromWeek(weekId, textId));
    }

    @Operation(summary = "Week에 Question 추가", description = "특정 Week에 Question을 추가합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "추가 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "Week를 찾을 수 없음")
    })
    @PostMapping("/{weekId}/questions")
    public ResponseEntity<WeekResponse> addQuestionToWeek(
            @PathVariable Long weekId,
            @RequestBody Question question) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(weekService.addQuestionToWeek(weekId, question));
    }

    @Operation(summary = "Week에서 Question 제거", description = "특정 Week에서 Question을 제거합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "제거 성공"),
            @ApiResponse(responseCode = "404", description = "Week 또는 Question을 찾을 수 없음")
    })
    @DeleteMapping("/{weekId}/questions/{questionId}")
    public ResponseEntity<WeekResponse> removeQuestionFromWeek(
            @PathVariable Long weekId,
            @PathVariable Long questionId) {
        return ResponseEntity.ok(weekService.removeQuestionFromWeek(weekId, questionId));
    }

    @Operation(summary = "Week에 SupplementalFile 추가", description = "특정 Week에 보충 자료 파일을 추가합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "추가 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "Week를 찾을 수 없음")
    })
    @PostMapping("/{weekId}/files")
    public ResponseEntity<WeekResponse> addSupplementalFileToWeek(
            @PathVariable Long weekId,
            @RequestBody SupplementalFile file) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(weekService.addSupplementalFileToWeek(weekId, file));
    }

    @Operation(summary = "Week에서 SupplementalFile 제거", description = "특정 Week에서 보충 자료 파일을 제거합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "제거 성공"),
            @ApiResponse(responseCode = "404", description = "Week 또는 파일을 찾을 수 없음")
    })
    @DeleteMapping("/{weekId}/files/{fileId}")
    public ResponseEntity<WeekResponse> removeSupplementalFileFromWeek(
            @PathVariable Long weekId,
            @PathVariable Long fileId) {
        return ResponseEntity.ok(weekService.removeSupplementalFileFromWeek(weekId, fileId));
    }

    @Operation(summary = "Week에 Quiz 추가", description = "특정 Week에 Quiz를 추가합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "추가 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "Week를 찾을 수 없음")
    })
    @PostMapping("/{weekId}/quizzes")
    public ResponseEntity<WeekResponse> addQuizToWeek(
            @PathVariable Long weekId,
            @RequestBody QuizWeekMapping quizMapping) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(weekService.addQuizToWeek(weekId, quizMapping));
    }

    @Operation(summary = "Week에서 Quiz 제거", description = "특정 Week에서 Quiz를 제거합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "제거 성공"),
            @ApiResponse(responseCode = "404", description = "Week 또는 Quiz 매핑을 찾을 수 없음")
    })
    @DeleteMapping("/{weekId}/quizzes/{quizMappingId}")
    public ResponseEntity<WeekResponse> removeQuizFromWeek(
            @PathVariable Long weekId,
            @PathVariable Long quizMappingId) {
        return ResponseEntity.ok(weekService.removeQuizFromWeek(weekId, quizMappingId));
    }
 */
}