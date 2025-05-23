package com._1.spring_rest_api.api.controller;

import com._1.spring_rest_api.api.dto.CourseResponse;
import com._1.spring_rest_api.api.dto.CreateCourseRequest;
import com._1.spring_rest_api.api.dto.CreateCourseResponse;
import com._1.spring_rest_api.api.dto.WeekResponse;
import com._1.spring_rest_api.entity.Course;
import com._1.spring_rest_api.entity.Week;
import com._1.spring_rest_api.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/course")
@RequiredArgsConstructor
public class CourseApiController {

    private final CourseService courseService;

    @PostMapping
    @Operation(method = "POST", description = "수업 생성")
    public ResponseEntity<CreateCourseResponse> createCourse(
            @RequestBody CreateCourseRequest request) {
        Long courseId = courseService.createCourse(request.getUserId(), request.getTitle(), request.getDescription());
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(courseId)
                .toUri();
        return ResponseEntity.created(location).body(new CreateCourseResponse(courseId));
    }

    @DeleteMapping("/{courseId}")
    @Operation(method = "DELETE", description = "수업 삭제")
    public ResponseEntity<Void> deleteCourse(
            @PathVariable Long courseId) {
        try {
            courseService.deleteCourse(courseId);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{courseId}")
    @Operation(method = "GET", description = "수업 ID로 조회")
    public ResponseEntity<CourseResponse> getCourseById(
            @PathVariable Long courseId) {
        try {
            return ResponseEntity.ok(courseService.getCourseById(courseId).toCourseResponse());
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/{userId}/courses")
    @Operation(method = "GET", description = "사용자 ID로 사용자가 가진 수업 리스트 조회")
    public ResponseEntity<List<CourseResponse>> getUserCourseList(
            @PathVariable Long userId) {
        List<Course> courseList = courseService.getCoursesByUserId(userId);
        List<CourseResponse> courseResponseList = courseList.stream()
                .map(Course::toCourseResponse)
                .toList();
        return ResponseEntity.ok(courseResponseList);
    }

    @GetMapping("/{courseId}/weeks")
    @Operation(method = "GET", description = "수업 ID로 해당 수업에 속한 주차 리스트 조회")
    public ResponseEntity<List<WeekResponse>> getCourseWeekList(
            @PathVariable Long courseId
    ) {
        List<Week> weekList = courseService.getCourseWeeksByCourseId(courseId);
        List<WeekResponse> weekResponseList = weekList.stream()
                .map(Week::toWeekResponse)
                .toList();
        return ResponseEntity.ok(weekResponseList);
    }
}
