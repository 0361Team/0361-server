package com._1.spring_rest_api.service;

import com._1.spring_rest_api.api.dto.TextResponse;
import com._1.spring_rest_api.entity.Text;
import com._1.spring_rest_api.repository.TextRepository;
import com._1.spring_rest_api.repository.WeekRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TextQueryServiceImpl implements TextQueryService{

    private final TextRepository textRepository;
    private final WeekRepository weekRepository;

    /**
     * 강의 텍스트 조회
     * @param textId 조회할 텍스트 ID
     * @return 텍스트 응답 DTO
     */
    public TextResponse getTextById(Long textId) {
        Text text = textRepository.findById(textId)
                .orElseThrow(() -> new EntityNotFoundException("Text not found with id: " + textId));

        return convertToTextResponse(text);
    }

    /**
     * 주차별 강의 텍스트 목록 조회
     * @param weekId 조회할 주차 ID
     * @return 텍스트 응답 DTO 목록
     */
    public List<TextResponse> getTextsByWeekId(Long weekId) {
        // 주차 존재 확인
        if (!weekRepository.existsById(weekId)) {
            throw new EntityNotFoundException("Week not found with id: " + weekId);
        }

        List<Text> texts = textRepository.findAllByWeekId(weekId);
        return texts.stream()
                .map(this::convertToTextResponse)
                .collect(Collectors.toList());
    }

    private TextResponse convertToTextResponse(Text text) {
        return TextResponse.builder()
                .id(text.getId())
                .weekId(text.getWeek() != null ? text.getWeek().getId() : null)
                .content(text.getContent())
                .type(text.getType())
                .build();
    }
}
