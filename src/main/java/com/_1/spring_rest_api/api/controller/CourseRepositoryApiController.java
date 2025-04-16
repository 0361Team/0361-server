package com._1.spring_rest_api.api.controller;

import com._1.spring_rest_api.api.dto.CourseResponse;
import com._1.spring_rest_api.api.dto.CreateCourseRequest;
import com._1.spring_rest_api.api.dto.CreateCourseResonse;
import com._1.spring_rest_api.api.dto.WeekResponse;
import com._1.spring_rest_api.entity.Course;
import com._1.spring_rest_api.entity.Week;
import com._1.spring_rest_api.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/course")
@RequiredArgsConstructor
public class CourseRepositoryApiController {

    private final CourseService courseService;

    @PostMapping
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
    public ResponseEntity<CourseResponse> getCourseById(
            @PathVariable Long courseId) {
        try {
            return ResponseEntity.ok(courseService.getCourseById(courseId).toCourseResponse());
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/{userId}/courses")
    public ResponseEntity<List<CourseResponse>> getUserCourseList(
            @PathVariable Long userId) {
        List<Course> courseList = courseService.getCoursesByUserId(userId);
        List<CourseResponse> courseResponseList = courseList.stream()
                .map(Course::toCourseResponse)
                .toList();
        return ResponseEntity.ok(courseResponseList);
    }

    @GetMapping("/{courseId}/weeks")
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
