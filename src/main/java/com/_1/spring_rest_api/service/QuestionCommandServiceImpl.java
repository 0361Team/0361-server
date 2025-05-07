package com._1.spring_rest_api.service;

import com._1.spring_rest_api.api.dto.FlashcardDTO;
import com._1.spring_rest_api.api.dto.FlashcardsRequest;
import com._1.spring_rest_api.entity.Question;
import com._1.spring_rest_api.entity.Week;
import com._1.spring_rest_api.repository.QuestionRepository;
import com._1.spring_rest_api.repository.WeekRepository;
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


    @Override
    public List<Long> saveFlashcards(Long weekId, FlashcardsRequest request) {
        Week week = weekRepository.findById(weekId)
                .orElseThrow(() -> new EntityNotFoundException("Week not found with id: " + weekId));

        List<Long> savedQuestionIds = new ArrayList<>();

        for (FlashcardDTO flashcard : request.getFlashcards()) {
            Question question = Question.builder()
                    .week(week)
                    .front(flashcard.getFront())
                    .back(flashcard.getBack())
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
