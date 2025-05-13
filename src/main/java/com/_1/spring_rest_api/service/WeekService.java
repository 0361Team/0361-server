package com._1.spring_rest_api.service;

import com._1.spring_rest_api.api.dto.WeekResponse;
import com._1.spring_rest_api.entity.*;
import com._1.spring_rest_api.repository.CourseRepository;
import com._1.spring_rest_api.repository.WeekRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class WeekService {

    private final WeekRepository weekRepository;
    private final CourseRepository courseRepository;

    @Transactional(readOnly = true)
    public List<WeekResponse> getAllWeeks() {
        return weekRepository.findAll().stream()
                .map(Week::toWeekResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<WeekResponse> getWeeksByCourseId(Long courseId) {
        List<Week> weeks = weekRepository.findByCourseId(courseId);
        List<WeekResponse> responses = new ArrayList<>();
        for (Week week : weeks) {
            try {
                responses.add(week.toWeekResponse());
            } catch (Exception e) {
                System.err.println("Week ID " + week.getId() + " 변환 중 오류 발생: " + e.getMessage());
            }
        }
        return responses;
    }

    @Transactional(readOnly = true)
    public WeekResponse getWeekById(Long weekId) {
        Week week = weekRepository.findById(weekId)
                .orElseThrow(() -> new NoSuchElementException("Week not found with id: " + weekId));
        return week.toWeekResponse();
    }

    public WeekResponse createWeek(Long courseId, String title, Integer weekNumber) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NoSuchElementException("Course not found with id: " + courseId));

        Week week = Week.builder()
                .title(title)
                .weekNumber(weekNumber)
                .build();

        week.changeCourse(course);
        weekRepository.save(week);

        return week.toWeekResponse();
    }

    public WeekResponse updateWeek(Long weekId, String title, Integer weekNumber) {
        Week week = weekRepository.findById(weekId)
                .orElseThrow(() -> new NoSuchElementException("Week not found with id: " + weekId));

        // 업데이트할 필드가 null이 아닌 경우에만 업데이트
        if (title != null) {
            week = Week.builder()
                    .id(week.getId())
                    .title(title)
                    .weekNumber(weekNumber != null ? weekNumber : week.getWeekNumber())
                    .course(week.getCourse())
                    .texts(week.getTexts())
                    .questions(week.getQuestions())
                    .supplementalFiles(week.getSupplementalFiles())
                    .quizWeekMappings(week.getQuizWeekMappings())
                    .build();
        } else if (weekNumber != null) {
            week = Week.builder()
                    .id(week.getId())
                    .title(week.getTitle())
                    .weekNumber(weekNumber)
                    .course(week.getCourse())
                    .texts(week.getTexts())
                    .questions(week.getQuestions())
                    .supplementalFiles(week.getSupplementalFiles())
                    .quizWeekMappings(week.getQuizWeekMappings())
                    .build();
        }

        weekRepository.save(week);
        return week.toWeekResponse();
    }

    public void deleteWeek(Long weekId) {
        Week week = weekRepository.findById(weekId)
                .orElseThrow(() -> new NoSuchElementException("Week not found with id: " + weekId));

        // 양방향 연관관계 정리
        if (week.getCourse() != null) {
            week.getCourse().getWeeks().remove(week);
        }

        weekRepository.delete(week);
    }

    public WeekResponse addTextToWeek(Long weekId, Text text) {
        Week week = weekRepository.findById(weekId)
                .orElseThrow(() -> new NoSuchElementException("Week not found with id: " + weekId));

        week.addText(text);
        weekRepository.save(week);

        return week.toWeekResponse();
    }

    public WeekResponse removeTextFromWeek(Long weekId, Long textId) {
        Week week = weekRepository.findById(weekId)
                .orElseThrow(() -> new NoSuchElementException("Week not found with id: " + weekId));

        week.getTexts().stream()
                .filter(text -> text.getId().equals(textId))
                .findFirst()
                .ifPresent(week::removeText);

        weekRepository.save(week);

        return week.toWeekResponse();
    }

    public WeekResponse addQuestionToWeek(Long weekId, Question question) {
        Week week = weekRepository.findById(weekId)
                .orElseThrow(() -> new NoSuchElementException("Week not found with id: " + weekId));

        week.addQuestion(question);
        weekRepository.save(week);

        return week.toWeekResponse();
    }

    public WeekResponse removeQuestionFromWeek(Long weekId, Long questionId) {
        Week week = weekRepository.findById(weekId)
                .orElseThrow(() -> new NoSuchElementException("Week not found with id: " + weekId));

        week.getQuestions().stream()
                .filter(question -> question.getId().equals(questionId))
                .findFirst()
                .ifPresent(week::removeQuestion);

        weekRepository.save(week);

        return week.toWeekResponse();
    }

    public WeekResponse addSupplementalFileToWeek(Long weekId, SupplementalFile file) {
        Week week = weekRepository.findById(weekId)
                .orElseThrow(() -> new NoSuchElementException("Week not found with id: " + weekId));

        week.addSupplementalFile(file);
        weekRepository.save(week);

        return week.toWeekResponse();
    }

    public WeekResponse removeSupplementalFileFromWeek(Long weekId, Long fileId) {
        Week week = weekRepository.findById(weekId)
                .orElseThrow(() -> new NoSuchElementException("Week not found with id: " + weekId));

        week.getSupplementalFiles().stream()
                .filter(file -> file.getId().equals(fileId))
                .findFirst()
                .ifPresent(week::removeSupplementalFile);

        weekRepository.save(week);

        return week.toWeekResponse();
    }

    public WeekResponse addQuizToWeek(Long weekId, QuizWeekMapping quizMapping) {
        Week week = weekRepository.findById(weekId)
                .orElseThrow(() -> new NoSuchElementException("Week not found with id: " + weekId));

        week.addQuizWeekMapping(quizMapping);
        weekRepository.save(week);

        return week.toWeekResponse();
    }

    public WeekResponse removeQuizFromWeek(Long weekId, Long quizMappingId) {
        Week week = weekRepository.findById(weekId)
                .orElseThrow(() -> new NoSuchElementException("Week not found with id: " + weekId));

        week.getQuizWeekMappings().stream()
                .filter(mapping -> mapping.getId().equals(quizMappingId))
                .findFirst()
                .ifPresent(week::removeQuizWeekMapping);

        weekRepository.save(week);

        return week.toWeekResponse();
    }
}