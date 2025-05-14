package com._1.spring_rest_api.apipayload.exception;

import com._1.spring_rest_api.apipayload.code.BaseErrorCode;
import com._1.spring_rest_api.apipayload.code.ErrorReasonDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GeneralException extends RuntimeException {
    private final BaseErrorCode code;

    public ErrorReasonDto getErrorReason() {
        return this.code.getReason();
    }

    public ErrorReasonDto getErrorReasonHttpStatus() {
        return this.code.getReasonHttpStatus();
    }
}