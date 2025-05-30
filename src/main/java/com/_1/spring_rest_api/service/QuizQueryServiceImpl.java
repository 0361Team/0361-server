package com._1.spring_rest_api.service;

import com._1.spring_rest_api.api.dto.*;
import com._1.spring_rest_api.entity.*;
import com._1.spring_rest_api.repository.CustomQuizRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class QuizQueryServiceImpl implements QuizQueryService{

    private final CustomQuizRepository customQuizRepository;

    @Override
    public List<QuizSummaryResponse> getQuizzesByUserId(Long userId) {
        List<CustomQuiz> quizzes = customQuizRepository.findAllByCreatorId(userId);

        return quizzes.stream()
                .map(this::convertToSummaryResponse)
                .collect(Collectors.toList());
    }

    @Override
    public QuizDetailResponse getQuizDetail(Long quizId) {
        CustomQuiz quiz = customQuizRepository.findByIdWithQuestions(quizId)
                .orElseThrow(() -> new EntityNotFoundException("Quiz not found with id: " + quizId));

        customQuizRepository.findByIdWithWeeks(quizId);

        return convertToDetailResponse(quiz);
    }

    // CustomQuiz 엔티티를 QuizSummaryResponse DTO로 변환
    private QuizSummaryResponse convertToSummaryResponse(CustomQuiz quiz) {
        int weekCount = quiz.getQuizWeekMappings().size();

        return QuizSummaryResponse.builder()
                .id(quiz.getId())
                .title(quiz.getTitle())
                .description(quiz.getDescription())
                .quizType(quiz.getQuizType())
                .totalQuestions(quiz.getTotalQuestions())
                .weekCount(weekCount)
                .createdAt(quiz.getCreateAt())
                .build();
    }

    // CustomQuiz 엔티티를 QuizDetailResponse DTO로 변환
    private QuizDetailResponse convertToDetailResponse(CustomQuiz quiz) {
        // 1. 연관된 주차 정보 변환
        List<WeekSummaryResponse> weeks = quiz.getQuizWeekMappings().stream()
                .map(QuizWeekMapping::getWeek)
                .map(this::convertToWeekSummaryResponse)
                .collect(Collectors.toList());

        // 2. 포함된 질문 정보 변환
        List<QuestionResponse> questions = quiz.getQuizQuestionMappings().stream()
                .map(QuizQuestionMapping::getQuestion)
                .map(Question::toQuestionResponse)
                .collect(Collectors.toList());

        // 3. 생성자 정보 변환
        UserSummaryResponse creator = UserSummaryResponse.builder()
                .id(quiz.getCreator().getId())
                .name(quiz.getCreator().getName())
                .email(quiz.getCreator().getEmail())
                .build();

        // 4. 최종 응답 DTO 생성
        return QuizDetailResponse.builder()
                .id(quiz.getId())
                .title(quiz.getTitle())
                .description(quiz.getDescription())
                .quizType(quiz.getQuizType())
                .totalQuestions(quiz.getTotalQuestions())
                .creator(creator)
                .weeks(weeks)
                .questions(questions)
                .createdAt(quiz.getCreateAt())
                .modifiedAt(quiz.getModifiedAt())
                .build();
    }

    // Week 엔티티를 WeekSummaryResponse DTO로 변환
    private WeekSummaryResponse convertToWeekSummaryResponse(Week week) {
        return WeekSummaryResponse.builder()
                .id(week.getId())
                .title(week.getTitle())
                .weekNumber(week.getWeekNumber())
                .courseId(week.getCourse().getId())
                .courseTitle(week.getCourse().getTitle())
                .build();
    }
}
