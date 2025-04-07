package com._1.spring_rest_api.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class CourseResponse {

    private Long id;
    private String title;
    private String description;
    private List<WeekResponse> weeks;
}
