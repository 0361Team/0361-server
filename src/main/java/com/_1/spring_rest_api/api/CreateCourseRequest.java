package com._1.spring_rest_api.api;

import lombok.Getter;

@Getter
public class CreateCourseRequest {

    private Long userId;
    private String title;
    private String description;
}
