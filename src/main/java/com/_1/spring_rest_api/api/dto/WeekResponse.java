package com._1.spring_rest_api.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WeekResponse {

    private Long id;
    private CourseResponse course;
    private String title;
}
