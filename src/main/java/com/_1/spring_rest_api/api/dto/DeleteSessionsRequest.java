package com._1.spring_rest_api.api.dto;

import jakarta.validation.constraints.NotEmpty;
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
public class DeleteSessionsRequest {

    @NotEmpty(message = "삭제할 세션 ID 목록은 필수입니다")
    @Size(max = 100, message = "한 번에 최대 100개 세션까지 삭제 가능합니다")
    private List<Long> sessionIds;
}