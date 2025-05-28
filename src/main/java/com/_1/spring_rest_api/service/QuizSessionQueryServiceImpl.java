package com._1.spring_rest_api.service;

import com._1.spring_rest_api.api.dto.QuizSessionDetailResponse;
import com._1.spring_rest_api.api.dto.QuizSessionResponse;
import com._1.spring_rest_api.api.dto.UserAnswerResponse;
import com._1.spring_rest_api.converter.QuizSessionConverter;
import com._1.spring_rest_api.converter.QuizSessionDetailConverter;
import com._1.spring_rest_api.converter.UserAnswerConverter;
import com._1.spring_rest_api.entity.CustomQuiz;
import com._1.spring_rest_api.entity.QuizSession;
import com._1.spring_rest_api.entity.UserAnswer;
import com._1.spring_rest_api.repository.QuizSessionRepository;
import com._1.spring_rest_api.repository.UserAnswerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuizSessionQueryServiceImpl implements QuizSessionQueryService {

    private final QuizSessionRepository quizSessionRepository;
    private final UserAnswerRepository userAnswerRepository;
    private final QuizSessionConverter quizSessionConverter;
    private final UserAnswerConverter userAnswerConverter;
    private final QuizSessionDetailConverter quizSessionDetailConverter;

    @Override
    public List<QuizSessionResponse> getUserQuizSessions(Long userId) {
        List<QuizSession> sessions = quizSessionRepository.findAllByUserId(userId);

        return sessions.stream()
                .map(quizSessionConverter::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public QuizSessionDetailResponse getSessionDetail(Long sessionId) {
        QuizSession session = quizSessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Session not found with id: " + sessionId));

        List<UserAnswerResponse> userAnswerResponses = getUserAnswersForSession(sessionId);

        return quizSessionDetailConverter.toDto(session, userAnswerResponses);
    }

    private List<UserAnswerResponse> getUserAnswersForSession(Long sessionId) {
        List<UserAnswer> userAnswers = userAnswerRepository.findByQuizSessionIdOrderByAnsweredAt(sessionId);

        // DTO로 변환
        return userAnswers.stream()
                .map(userAnswerConverter::toDto)
                .collect(Collectors.toList());
    }
}
