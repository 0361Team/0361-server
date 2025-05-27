package com._1.spring_rest_api.service;

import com._1.spring_rest_api.api.dto.CreateQuizRequest;
import com._1.spring_rest_api.entity.*;
import com._1.spring_rest_api.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class QuizCommandServiceImpl implements QuizCommandService{

    private final QuestionRepository questionRepository;
    private final WeekRepository weekRepository;
    private final UserRepository userRepository;
    private final CustomQuizRepository customQuizRepository;
    private final QuizQuestionMappingRepository quizQuestionMappingRepository;
    private final QuizWeekMappingRepository quizWeekMappingRepository;
    private final QuizSessionRepository quizSessionRepository;

    /**
     * 사용자 ID, 주차 ID 리스트, 그리고 질문 ID 리스트(선택)를 기반으로 새 퀴즈를 생성합니다.
     */
    @Override
    public Long createQuiz(CreateQuizRequest request) {
        // 사용자 찾기
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + request.getUserId()));

        // 새 퀴즈 엔티티 생성
        CustomQuiz quiz = CustomQuiz.create(user, request.getTitle(), request.getDescription(), request.getQuizType());

        // 퀴즈 저장 (ID 발급)
        CustomQuiz savedQuiz = customQuizRepository.save(quiz);

        // 주차 연결
        connectWeeksToQuiz(request.getWeekIds(), savedQuiz);

        // 질문 처리
        processQuestions(request, savedQuiz);

        return savedQuiz.getId();
    }

    @Override
    public void deleteQuiz(Long quizId) {
        if (!customQuizRepository.existsById(quizId)) {
            throw new EntityNotFoundException("Quiz not found with id: " + quizId);
        }
        customQuizRepository.deleteById(quizId);
    }

    @Override
    public Long startQuizSession(Long quizId, Long userId) {
        // 퀴즈 및 사용자 존재 확인
        CustomQuiz quiz = customQuizRepository.findById(quizId)
                .orElseThrow(() -> new EntityNotFoundException("Quiz not found with id: " + quizId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        // 새 퀴즈 세션 생성
        QuizSession session = QuizSession.create(user, quiz);

        // 양방향 연관관계 설정
        user.addQuizSession(session);
        quiz.addQuizSession(session);

        // 저장 및 ID 반환
        QuizSession savedSession = quizSessionRepository.save(session);
        return savedSession.getId();
    }

    // 주차와 퀴즈 연결
    private void connectWeeksToQuiz(List<Long> weekIds, CustomQuiz quiz) {
        if (weekIds == null || weekIds.isEmpty()) {
            return;
        }

        for (Long weekId : weekIds) {
            Week week = weekRepository.findById(weekId)
                    .orElseThrow(() -> new EntityNotFoundException("Week not found with id: " + weekId));

            // 주차와 퀴즈 연결 저장 (도메인 로직 활용)
            QuizWeekMapping mapping = QuizWeekMapping.builder()
                    .quiz(quiz)
                    .week(week)
                    .build();

            // 양방향 연관관계 설정
            quiz.addQuizWeekMapping(mapping);
        }
    }

    // 질문 처리 로직
    private void processQuestions(CreateQuizRequest request, CustomQuiz quiz) {
        Set<Question> questions = new HashSet<>();

        // 주차 기반 질문 선택
        if (request.getWeekIds() != null && !request.getWeekIds().isEmpty()) {
            // 선택된 모든 주차의 질문 가져오기
            List<Question> weekQuestions = getQuestionList(request);

            // 질문 수 제한이 있는 경우 랜덤 선택
            weekQuestions = getRandomQuestionsIfHasLimit(request, weekQuestions);

            questions.addAll(weekQuestions);
        }

        // 질문 중복 제거 및 퀴즈에 추가
        for (Question question : questions) {
            QuizQuestionMapping mapping = QuizQuestionMapping.builder()
                    .quiz(quiz)
                    .question(question)
                    .build();

            question.addQuizQuestionMapping(mapping);
            quiz.addQuizQuestionMapping(mapping);
        }

        // 총 질문 수 업데이트 및 저장
        quiz.updateTotalQuestions(questions.size());
        customQuizRepository.save(quiz);
    }

    private static List<Question> getRandomQuestionsIfHasLimit(CreateQuizRequest request, List<Question> weekQuestions) {
        if (request.getQuestionCount() != null && request.getQuestionCount() > 0 &&
                request.getQuestionCount() < weekQuestions.size()) {
            // 무작위 선택
            Collections.shuffle(weekQuestions);
            weekQuestions = weekQuestions.subList(0, request.getQuestionCount());
        }
        return weekQuestions;
    }

    private List<Question> getQuestionList(CreateQuizRequest request) {
        List<Question> weekQuestions = new ArrayList<>();
        for (Long weekId : request.getWeekIds()) {
            weekQuestions.addAll(questionRepository.findAllByWeekId(weekId));
        }
        return weekQuestions;
    }
}
