package com._1.spring_rest_api.service;

import com._1.spring_rest_api.api.dto.AnswerRequest;
import com._1.spring_rest_api.api.dto.AnswerResponse;
import com._1.spring_rest_api.api.dto.DeleteSessionsRequest;
import com._1.spring_rest_api.api.dto.DeleteSessionsResponse;
import com._1.spring_rest_api.converter.AnswerResponseConverter;
import com._1.spring_rest_api.entity.Question;
import com._1.spring_rest_api.entity.QuizSession;
import com._1.spring_rest_api.entity.User;
import com._1.spring_rest_api.entity.UserAnswer;
import com._1.spring_rest_api.repository.QuizSessionRepository;
import com._1.spring_rest_api.repository.UserAnswerRepository;
import com._1.spring_rest_api.repository.UserRepository;
import com._1.spring_rest_api.security.SecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class QuizSessionCommandServiceImpl implements QuizSessionCommandService {

    private final QuizSessionRepository quizSessionRepository;
    private final UserAnswerRepository userAnswerRepository;
    private final UserRepository userRepository;
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

    @Override
    public DeleteSessionsResponse deleteSessions(DeleteSessionsRequest request) {
        // JWT 토큰에서 현재 사용자 정보 추출
        User currentUser = getCurrentUser();
        List<Long> sessionIds = request.getSessionIds();
        List<Long> deletedSessionIds = new ArrayList<>();
        List<DeleteSessionsResponse.SessionDeleteFailure> failures = new ArrayList<>();

        log.info("배치 세션 삭제 시작: userId={}, sessionCount={}", currentUser.getId(), sessionIds.size());

        for (Long sessionId : sessionIds) {
            try {
                QuizSession session = findSessionById(sessionId);

                // 권한 검증 - 세션 소유자와 현재 사용자가 일치하는지 확인
                validateUserPermission(session, currentUser);

                quizSessionRepository.delete(session);
                deletedSessionIds.add(sessionId);

                log.debug("세션 삭제 성공: sessionId={}", sessionId);

            } catch (EntityNotFoundException e) {
                failures.add(createFailure(sessionId, "세션을 찾을 수 없습니다", "SESSION_NOT_FOUND"));
                log.warn("세션을 찾을 수 없음: sessionId={}", sessionId);
            } catch (SecurityException e) {
                failures.add(createFailure(sessionId, "삭제 권한이 없습니다", "PERMISSION_DENIED"));
                log.warn("권한 없는 삭제 시도: sessionId={}, userId={}", sessionId, currentUser.getId());
            } catch (Exception e) {
                log.error("세션 삭제 중 예상치 못한 오류: sessionId={}", sessionId, e);
                failures.add(createFailure(sessionId, "알 수 없는 오류가 발생했습니다", "UNKNOWN_ERROR"));
            }
        }

        int successCount = deletedSessionIds.size();
        int failureCount = failures.size();

        log.info("배치 세션 삭제 완료: userId={}, total={}, success={}, failure={}",
                currentUser.getId(), sessionIds.size(), successCount, failureCount);

        return DeleteSessionsResponse.builder()
                .totalRequested(sessionIds.size())
                .successCount(successCount)
                .failureCount(failureCount)
                .deletedSessionIds(deletedSessionIds)
                .failures(failures)
                .build();
    }

    private User getCurrentUser() {
        String email = SecurityUtils.getCurrentUserEmail();
        if (email == null) {
            throw new AccessDeniedException("인증되지 않은 사용자입니다");
        }

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));
    }


    private QuizSession findSessionById(Long sessionId) {
        return quizSessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Session not found with id: " + sessionId));
    }

    private void validateUserPermission(QuizSession session, User user) {
        if (!session.getUser().getId().equals(user.getId())) {
            throw new SecurityException("해당 세션에 대한 삭제 권한이 없습니다.");
        }
    }

    private DeleteSessionsResponse.SessionDeleteFailure createFailure(Long sessionId, String reason, String errorCode) {
        return DeleteSessionsResponse.SessionDeleteFailure.builder()
                .sessionId(sessionId)
                .reason(reason)
                .errorCode(errorCode)
                .build();
    }
}