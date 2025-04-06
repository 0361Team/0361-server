package com._1.spring_rest_api.service;

import com._1.spring_rest_api.entity.Course;
import com._1.spring_rest_api.entity.User;
import com._1.spring_rest_api.entity.Week;
import com._1.spring_rest_api.repository.CourseRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseService {

    private final CourseRepository courseRepository;

    protected Long createCourse(
            Long userId, String title, String description) {
        // User entity 조회 후 넣어야 함. - 임시로 User() 사용
        Course course = courseRepository.save(new Course(new User(userId), title, description));
        return course.getId();
    }

    protected void deleteCourse(Long courseId) {
        if (!courseRepository.existsById(courseId)) {
            throw new EntityNotFoundException("Course not found with id: " + courseId);
        }
        courseRepository.deleteById(courseId);
    }

    @Transactional(readOnly = true)
    public Course getCourseById(Long courseId) {
        return courseRepository.findById(courseId).orElseThrow(
                () -> new EntityNotFoundException("Course not found with id: " + courseId));
    }

    @Transactional(readOnly = true)
    public List<Course> getCoursesByUserId(Long userId) {
        return courseRepository.findCoursesByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<Week> getCourseWeeksByCourseId(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found with id: " + courseId));
        return course.getWeeks();
    }

}