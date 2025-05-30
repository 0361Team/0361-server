package com._1.spring_rest_api.service;

import com._1.spring_rest_api.api.dto.AnswerRequest;
import com._1.spring_rest_api.api.dto.AnswerResponse;
import com._1.spring_rest_api.converter.AnswerResponseConverter;
import com._1.spring_rest_api.converter.QuestionConverter;
import com._1.spring_rest_api.entity.Question;
import com._1.spring_rest_api.entity.QuizSession;
import com._1.spring_rest_api.entity.UserAnswer;
import com._1.spring_rest_api.repository.QuizSessionRepository;
import com._1.spring_rest_api.repository.UserAnswerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class QuizSessionCommandServiceImpl implements QuizSessionCommandService {

    private final QuizSessionRepository quizSessionRepository;
    private final UserAnswerRepository userAnswerRepository;
    private final AnswerResponseConverter answerResponseConverter;

    @Override
    public AnswerResponse answerQuestion(Long sessionId, AnswerRequest request) {
        // 1. 세션 조회
        QuizSession session = findSessionById(sessionId);

        // 2. 현재 질문 확인
        Question currentQuestion = session.getCurrentQuestion();
        if (currentQuestion == null) {
            throw new EntityNotFoundException("Session Id not found" + sessionId);
        }

        // 3. 사용자 답변 생성 및 저장
        UserAnswer userAnswer = session.createAnswer(request.getUserAnswer());
        userAnswerRepository.save(userAnswer);

        // 4. 세션 상태 업데이트
        session.recordAnswer();
        quizSessionRepository.save(session);

        // 5. 응답 생성 및 반환
        Question nextQuestion = session.getNextQuestion();
        return answerResponseConverter.toDto(
                userAnswer.getIsCorrect(),
                currentQuestion.getBack(),
                nextQuestion,
                session.getCurrentQuestionIndex(),
                session.isComplete()
        );
    }

    @Override
    public void completeSession(Long sessionId) {
        QuizSession session = findSessionById(sessionId);

        if (!session.getCompleted()) {
            session.completeSession();
            quizSessionRepository.save(session);
        }
    }

    /**
     * ID로 퀴즈 세션 조회
     */
    private QuizSession findSessionById(Long sessionId) {
        return quizSessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Session not found with id: " + sessionId));
    }
}