package com._1.spring_rest_api.service;

import com._1.spring_rest_api.entity.User;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com._1.spring_rest_api.entity.Course;

import com._1.spring_rest_api.entity.Week;
import com._1.spring_rest_api.repository.CourseRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private CourseService courseService;

    private User testUser;
    private Course testCourse;
    private List<Week> testWeeks;

    @BeforeEach
    void setUp() {
        testUser = new User(1L);
        testCourse = new Course(1L, testUser, "Test Course", "Test Description");
        testWeeks = new ArrayList<>();

        Week week1 = new Week(1L, "Week 1", testCourse);
        Week week2 = new Week(2L, "Week 2", testCourse);

        testWeeks.add(week1);
        testWeeks.add(week2);

        testCourse.setWeeks(testWeeks);
    }

    @Test
    @DisplayName("코스 생성 성공 테스트")
    void createCourse_Success() {
        // Given
        when(courseRepository.save(any(Course.class))).thenAnswer(invocation -> {
            Course inputCourse = invocation.getArgument(0);
            ReflectionTestUtils.setField(inputCourse, "id", 1L); // ID 설정
            return inputCourse;
        });

        // When
        Long courseId = courseService.createCourse(1L, "Test Course", "Test Description");

        // Then
        assertNotNull(courseId);
        assertEquals(1L, courseId);
    }

    @Test
    @DisplayName("코스 삭제 성공 테스트")
    void deleteCourse_Success() {
        // Given
        when(courseRepository.existsById(1L)).thenReturn(true);
        doNothing().when(courseRepository).deleteById(1L);

        // When
        courseService.deleteCourse(1L);

        // Then
        verify(courseRepository, times(1)).existsById(1L);
        verify(courseRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("존재하지 않는 코스 삭제 시 예외 발생")
    void deleteCourse_CourseNotFound() {
        // Given
        when(courseRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> {
            courseService.deleteCourse(999L);
        });
        verify(courseRepository, times(1)).existsById(999L);
        verify(courseRepository, never()).deleteById(999L);
    }

    @Test
    @DisplayName("ID로 코스 조회 성공 테스트")
    void getCourseById_Success() {
        // Given
        when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));

        // When
        Course result = courseService.getCourseById(1L);

        // Then
        assertNotNull(result);
        System.out.println("result : " + result.getId());
        assertEquals(1L, result.getId());
        assertEquals("Test Course", result.getTitle());
        verify(courseRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("존재하지 않는 ID로 코스 조회 시 예외 발생")
    void getCourseById_CourseNotFound() {
        // Given
        when(courseRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> {
            courseService.getCourseById(999L);
        });
        verify(courseRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("사용자 ID로 코스 목록 조회 성공 테스트")
    void getCoursesByUserId_Success() {
        // Given
        List<Course> courses = new ArrayList<>();
        courses.add(testCourse);

        when(courseRepository.findCoursesByUserId(1L)).thenReturn(courses);

        // When
        List<Course> result = courseService.getCoursesByUserId(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Course", result.get(0).getTitle());
        verify(courseRepository, times(1)).findCoursesByUserId(1L);
    }

    @Test
    @DisplayName("코스 ID로 주차 목록 조회 성공 테스트")
    void getCourseWeeksByCourseId_Success() {
        // Given
        when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));

        // When
        List<Week> result = courseService.getCourseWeeksByCourseId(1L);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Week 1", result.get(0).getTitle());
        assertEquals("Week 2", result.get(1).getTitle());
        verify(courseRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("존재하지 않는 코스 ID로 주차 목록 조회 시 예외 발생")
    void getCourseWeeksByCourseId_CourseNotFound() {
        // Given
        when(courseRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> {
            courseService.getCourseWeeksByCourseId(999L);
        });
        verify(courseRepository, times(1)).findById(999L);
    }
}