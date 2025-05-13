package com._1.spring_rest_api.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TextResponse {
    private Long id;
    private Long weekId;
    private String content;
    private String type;
}