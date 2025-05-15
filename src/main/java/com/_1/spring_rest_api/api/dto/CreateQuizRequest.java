package com._1.spring_rest_api.api.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateQuizRequest {

    @NotNull(message = "사용자 ID는 필수입니다")
    private Long userId;

    @NotEmpty(message = "퀴즈 제목은 필수입니다")
    @Size(min = 1, max = 100, message = "퀴즈 제목은 1-100자 사이여야 합니다")
    private String title;

    private String description;

    @NotEmpty(message = "최소 하나 이상의 주차를 선택해야 합니다")
    private List<Long> weekIds;

    private String quizType; // 예: "WEEKLY", "CUSTOM"

    private Integer questionCount; // 랜덤으로 선택할 질문 수 (옵션)
}
