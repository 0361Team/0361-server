package com._1.spring_rest_api.service;

import com._1.spring_rest_api.api.dto.TextResponse;

import java.util.List;

public interface TextQueryService {
    TextResponse getTextById(Long textId);

    List<TextResponse> getTextsByWeekId(Long weekId);
}
