package com._1.spring_rest_api.service;

import com._1.spring_rest_api.api.dto.QuestionDto;
import com._1.spring_rest_api.entity.Question;
import com._1.spring_rest_api.entity.Week;
import com._1.spring_rest_api.repository.QuestionRepository;
import com._1.spring_rest_api.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class QuestionCommandServiceImpl implements QuestionCommandService {

    private final QuestionRepository questionRepository;
    private final WeekRepository weekRepository;
    private final ClaudeService claudeService;

    /**
     * 주차 내용을 기반으로 AI를 사용하여 자동으로 질문을 생성하고 저장합니다.
     * @param weekId 주차 ID
     * @param minQuestionCount 생성할 최소 질문 수
     * @return 생성된 질문 ID 목록
     */
    @Transactional
    public List<Long> generateAndSaveQuestions(Long weekId, int minQuestionCount) {
        // 주차 확인
        Week week = weekRepository.findById(weekId)
                .orElseThrow(() -> new EntityNotFoundException("Week not found with id: " + weekId));

        // AI를 사용하여 질문 생성
        List<QuestionDto> generatedQuestions =
                claudeService.generateQuestionsFromWeekTexts(weekId, minQuestionCount);

        // 생성된 질문을 저장
        List<Long> savedQuestionIds = new ArrayList<>();
        for (QuestionDto questionDto : generatedQuestions) {
            Question question = Question.builder()
                    .week(week)
                    .front(questionDto.getFront())
                    .back(questionDto.getBack())
                    .build();

            Question savedQuestion = questionRepository.save(question);
            savedQuestionIds.add(savedQuestion.getId());
        }

        return savedQuestionIds;
    }


    // Question 삭제
    public void deleteQuestion(Long questionId) {
        if (!questionRepository.existsById(questionId)) {
            throw new EntityNotFoundException("Question not found with id: " + questionId);
        }
        questionRepository.deleteById(questionId);
    }
}
