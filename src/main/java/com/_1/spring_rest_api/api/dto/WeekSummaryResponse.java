package com._1.spring_rest_api.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeekSummaryResponse {
    private Long id;
    private String title;
    private Integer weekNumber;
    private Long courseId;
    private String courseTitle;
}