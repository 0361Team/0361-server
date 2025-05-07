package com._1.spring_rest_api.service;

import com._1.spring_rest_api.api.dto.QuestionResponse;
import com._1.spring_rest_api.entity.Question;
import com._1.spring_rest_api.repository.QuestionRepository;
import com._1.spring_rest_api.repository.WeekRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class QuestionQueryServiceImpl implements QuestionQueryService {

    private final QuestionRepository questionRepository;
    private final WeekRepository weekRepository;

    // Week ID로 모든 Question 조회
    public List<QuestionResponse> getAllQuestionsByWeekId(Long weekId) {
        weekRepository.findById(weekId)
                .orElseThrow(() -> new EntityNotFoundException("Week not found with id: " + weekId));

        List<Question> questions = questionRepository.findAllByWeekId(weekId);

        return questions.stream()
                .map(Question::toQuestionResponse)
                .collect(Collectors.toList());
    }

    // ID로 Question 조회
    public QuestionResponse getQuestionById(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new EntityNotFoundException("Question not found with id: " + questionId));

        return question.toQuestionResponse();
    }
}
