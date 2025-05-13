package com._1.spring_rest_api.api.controller;

import com._1.spring_rest_api.api.dto.WeekRequest;
import com._1.spring_rest_api.api.dto.WeekResponse;
import com._1.spring_rest_api.entity.Question;
import com._1.spring_rest_api.entity.QuizWeekMapping;
import com._1.spring_rest_api.entity.SupplementalFile;
import com._1.spring_rest_api.entity.Text;
import com._1.spring_rest_api.service.WeekService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/weeks")
@RequiredArgsConstructor
public class WeekController {

    private final WeekService weekService;

    @GetMapping
    public ResponseEntity<List<WeekResponse>> getAllWeeks() {
        return ResponseEntity.ok(weekService.getAllWeeks());
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<WeekResponse>> getWeeksByCourseId(@PathVariable Long courseId) {
        return ResponseEntity.ok(weekService.getWeeksByCourseId(courseId));
    }

    @GetMapping("/{weekId}")
    public ResponseEntity<WeekResponse> getWeekById(@PathVariable Long weekId) {
        return ResponseEntity.ok(weekService.getWeekById(weekId));
    }

    @PostMapping
    public ResponseEntity<WeekResponse> createWeek(@RequestBody WeekRequest request) {
        WeekResponse response = weekService.createWeek(
                request.getCourseId(),
                request.getTitle(),
                request.getWeekNumber()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

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

    @DeleteMapping("/{weekId}")
    public ResponseEntity<Void> deleteWeek(@PathVariable Long weekId) {
        weekService.deleteWeek(weekId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{weekId}/texts")
    public ResponseEntity<WeekResponse> addTextToWeek(
            @PathVariable Long weekId,
            @RequestBody Text text) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(weekService.addTextToWeek(weekId, text));
    }

    @DeleteMapping("/{weekId}/texts/{textId}")
    public ResponseEntity<WeekResponse> removeTextFromWeek(
            @PathVariable Long weekId,
            @PathVariable Long textId) {
        return ResponseEntity.ok(weekService.removeTextFromWeek(weekId, textId));
    }

    @PostMapping("/{weekId}/questions")
    public ResponseEntity<WeekResponse> addQuestionToWeek(
            @PathVariable Long weekId,
            @RequestBody Question question) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(weekService.addQuestionToWeek(weekId, question));
    }

    @DeleteMapping("/{weekId}/questions/{questionId}")
    public ResponseEntity<WeekResponse> removeQuestionFromWeek(
            @PathVariable Long weekId,
            @PathVariable Long questionId) {
        return ResponseEntity.ok(weekService.removeQuestionFromWeek(weekId, questionId));
    }

    @PostMapping("/{weekId}/files")
    public ResponseEntity<WeekResponse> addSupplementalFileToWeek(
            @PathVariable Long weekId,
            @RequestBody SupplementalFile file) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(weekService.addSupplementalFileToWeek(weekId, file));
    }

    @DeleteMapping("/{weekId}/files/{fileId}")
    public ResponseEntity<WeekResponse> removeSupplementalFileFromWeek(
            @PathVariable Long weekId,
            @PathVariable Long fileId) {
        return ResponseEntity.ok(weekService.removeSupplementalFileFromWeek(weekId, fileId));
    }

    @PostMapping("/{weekId}/quizzes")
    public ResponseEntity<WeekResponse> addQuizToWeek(
            @PathVariable Long weekId,
            @RequestBody QuizWeekMapping quizMapping) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(weekService.addQuizToWeek(weekId, quizMapping));
    }

    @DeleteMapping("/{weekId}/quizzes/{quizMappingId}")
    public ResponseEntity<WeekResponse> removeQuizFromWeek(
            @PathVariable Long weekId,
            @PathVariable Long quizMappingId) {
        return ResponseEntity.ok(weekService.removeQuizFromWeek(weekId, quizMappingId));
    }
}