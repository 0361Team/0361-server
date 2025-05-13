package com._1.spring_rest_api.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TextCreateRequest {
    private Long weekId;
    private String content;
    private String type;  // 강의 텍스트 유형 (예: "LECTURE", "SUMMARY" 등)
}