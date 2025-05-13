package com._1.spring_rest_api.service;

import com._1.spring_rest_api.api.dto.TextCreateRequest;
import com._1.spring_rest_api.api.dto.TextUpdateRequest;
import com._1.spring_rest_api.entity.Text;
import com._1.spring_rest_api.entity.Week;
import com._1.spring_rest_api.repository.TextRepository;
import com._1.spring_rest_api.repository.WeekRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TextCommandServiceImpl implements TextCommandService {

    private final TextRepository textRepository;
    private final WeekRepository weekRepository;

    /**
     * 강의 텍스트 생성
     *
     * @param request 텍스트 생성 요청 DTO
     * @return 생성된 텍스트 ID
     */
    public Long createText(TextCreateRequest request) {
        // 주차 엔티티 조회
        Week week = weekRepository.findById(request.getWeekId())
                .orElseThrow(() -> new EntityNotFoundException("Week not found with id: " + request.getWeekId()));

        // Text 엔티티 생성 및 저장
        Text text = buildText(request, week);

        // 양방향 연관관계 설정
        text.changeWeek(week);

        Text savedText = textRepository.save(text);
        return savedText.getId();
    }

    private Text buildText(TextCreateRequest request, Week week) {
        return Text.builder()
                .week(week)
                .content(request.getContent())
                .type(request.getType())
                .build();
    }

    /**
     * 강의 텍스트 수정
     *
     * @param textId  수정할 텍스트 ID
     * @param request 텍스트 수정 요청 DTO
     */
    public void updateText(Long textId, TextUpdateRequest request) {
        Text text = textRepository.findById(textId)
                .orElseThrow(() -> new EntityNotFoundException("Text not found with id: " + textId));

        // 엔티티 수정 (내용 및 타입만 수정 가능)
        updateTextEntity(text, request);
    }

    /**
     * 강의 텍스트 삭제
     *
     * @param textId 삭제할 텍스트 ID
     */
    public void deleteText(Long textId) {
        if (!textRepository.existsById(textId)) {
            throw new EntityNotFoundException("Text not found with id: " + textId);
        }
        textRepository.deleteById(textId);
    }

    /**
     * 주차별 모든 강의 텍스트 삭제
     *
     * @param weekId 삭제할 주차 ID
     * @return 삭제된 텍스트 수
     */
    public int deleteAllTextsByWeekId(Long weekId) {
        if (!weekRepository.existsById(weekId)) {
            throw new EntityNotFoundException("Week not found with id: " + weekId);
        }

        List<Text> textsToDelete = textRepository.findAllByWeekId(weekId);
        textRepository.deleteAll(textsToDelete);

        return textsToDelete.size();
    }

    private void updateTextEntity(Text text, TextUpdateRequest request) {
        if (request.getContent() != null) {
            text.updateContent(request.getContent());
        }

        if (request.getType() != null) {
            text.updateType(request.getType());
        }
    }
}
