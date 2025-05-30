package com._1.spring_rest_api.converter;

import com._1.spring_rest_api.api.dto.QuestionResponse;
import com._1.spring_rest_api.api.dto.QuizSessionDetailResponse;
import com._1.spring_rest_api.api.dto.UserAnswerResponse;
import com._1.spring_rest_api.entity.CustomQuiz;
import com._1.spring_rest_api.entity.Question;
import com._1.spring_rest_api.entity.QuizSession;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class QuizSessionDetailConverter{

    public QuizSessionDetailResponse toDto(QuizSession session, List<UserAnswerResponse> userAnswers) {
        CustomQuiz quiz = session.getQuiz();

        // 현재 질문 응답 객체 생성
        QuestionResponse currentQuestion = getCurrentQuestionResponse(session);

        return QuizSessionDetailResponse.builder()
                .id(session.getId())
                .quizId(quiz.getId())
                .quizTitle(quiz.getTitle())
                .quizDescription(quiz.getDescription())
                .totalQuestions(quiz.getTotalQuestions())
                .currentQuestionIndex(session.getCurrentQuestionIndex())
                .currentQuestion(currentQuestion)
                .completed(session.getCompleted())
                .userAnswers(userAnswers)
                .createdAt(session.getCreateAt())
                .completedAt(session.getCompletedAt())
                .build();
    }

    private QuestionResponse getCurrentQuestionResponse(QuizSession session) {
        CustomQuiz quiz = session.getQuiz();
        int currentIndex = session.getCurrentQuestionIndex();

        if (currentIndex < quiz.getQuizQuestionMappings().size()) {
            Question question = quiz.getQuizQuestionMappings().get(currentIndex).getQuestion();
            return question.toQuestionResponse();
        }
        return null;
    }
}