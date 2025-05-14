package com._1.spring_rest_api.apipayload.code.status;

import com._1.spring_rest_api.apipayload.code.BaseErrorCode;
import com._1.spring_rest_api.apipayload.code.ErrorReasonDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {
    // 일반적인 응답
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON401", "인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

    // 사용자 관련 에러
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER404", "사용자를 찾을 수 없습니다."),

    // 코스 관련 에러
    COURSE_NOT_FOUND(HttpStatus.NOT_FOUND, "COURSE404", "코스를 찾을 수 없습니다."),

    // 주차 관련 에러
    WEEK_NOT_FOUND(HttpStatus.NOT_FOUND, "WEEK404", "주차를 찾을 수 없습니다."),

    // 질문 관련 에러
    QUESTION_NOT_FOUND(HttpStatus.NOT_FOUND, "QUESTION404", "질문을 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDto getReason() {
        return ErrorReasonDto.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .build();
    }

    @Override
    public ErrorReasonDto getReasonHttpStatus() {
        return ErrorReasonDto.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build();
    }
}
