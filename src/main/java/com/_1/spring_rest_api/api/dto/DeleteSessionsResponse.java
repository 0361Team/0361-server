package com._1.spring_rest_api.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeleteSessionsResponse {

    private Integer totalRequested;        // 삭제 요청된 총 세션 수
    private Integer successCount;          // 성공적으로 삭제된 세션 수
    private Integer failureCount;          // 삭제 실패한 세션 수
    private List<Long> deletedSessionIds;  // 삭제된 세션 ID 목록
    private List<SessionDeleteFailure> failures; // 실패한 세션 정보

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SessionDeleteFailure {
        private Long sessionId;      // 실패한 세션 ID
        private String reason;       // 실패 이유 (사용자용 메시지)
        private String errorCode;    // 오류 코드 (클라이언트 처리용)
    }
}