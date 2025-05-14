package com._1.spring_rest_api.service;

import com._1.spring_rest_api.api.dto.TextCreateRequest;
import com._1.spring_rest_api.api.dto.TextUpdateRequest;

public interface TextCommandService {

    Long createText(TextCreateRequest request);

    void updateText(Long textId, TextUpdateRequest request);

    void deleteText(Long textId);

    int deleteAllTextsByWeekId(Long weekId);
}
