package com._1.spring_rest_api.api;

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
    public ResponseEntity<Long> createCourse(
            @RequestBody CreateCourseRequest request) {
        Long courseId = courseService.createCourse(request.getUserId(), request.getTitle(), request.getDescription());
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(courseId)
                .toUri();
        return ResponseEntity.created(location).body(courseId);
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<Void> deleteCourse(
            @PathVariable Long courseId) {
        courseService.deleteCourse(courseId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/course-id/{courseId}")
    public ResponseEntity<Course> getCourseById(
            @PathVariable Long courseId) {
        return ResponseEntity.ok(courseService.getCourseById(courseId));
    }

    @GetMapping("/user/{userId}/courses")
    public ResponseEntity<List<Course>> getUserCourseList(
            @PathVariable Long userId) {
        return ResponseEntity.ok(courseService.getCoursesByUserId(userId));
    }

    @GetMapping("/course/{courseId}/weeks")
    public ResponseEntity<List<Week>> getCourseWeekList(
            @PathVariable Long courseId
    ) {
        return ResponseEntity.ok(courseService.getCourseWeeksByCourseId(courseId));
    }
}
