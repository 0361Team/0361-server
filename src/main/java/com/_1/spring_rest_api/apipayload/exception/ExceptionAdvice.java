package com._1.spring_rest_api.apipayload.exception;

import com._1.spring_rest_api.apipayload.ApiResponse;
import com._1.spring_rest_api.apipayload.code.ErrorReasonDto;
import com._1.spring_rest_api.apipayload.code.status.ErrorStatus;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class ExceptionAdvice {

    // FieldErrorDto 내부 클래스 정의
    @Getter
    @AllArgsConstructor
    public static class FieldErrorDto {
        private String field;
        private String message;
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleEntityNotFoundException(EntityNotFoundException e) {
        log.warn("Entity not found: {}", e.getMessage());

        // 메시지에서 엔티티 유형을 추출하여 적절한 에러 상태 결정
        ErrorStatus errorStatus = getErrorStatusFromMessage(e.getMessage());

        ApiResponse<Object> apiResponse = ApiResponse.onFailure(
                errorStatus.getCode(),
                e.getMessage(),
                null);
        return ResponseEntity.status(errorStatus.getHttpStatus()).body(apiResponse);
    }

    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneralException(GeneralException e) {
        log.error("General exception occurred: {}", e.getMessage());
        ErrorReasonDto errorReasonDto = e.getErrorReasonHttpStatus();
        ApiResponse<Object> apiResponse = ApiResponse.onFailure(
                errorReasonDto.getCode(),
                errorReasonDto.getMessage(),
                null
        );
        return ResponseEntity.status(errorReasonDto.getHttpStatus()).body(apiResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<List<FieldErrorDto>>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.warn("Method argument validation failed: {}", e.getMessage());

        List<FieldErrorDto> fieldErrors = new ArrayList<>();
        BindingResult bindingResult = e.getBindingResult();

        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            fieldErrors.add(new FieldErrorDto(
                    fieldError.getField(),
                    fieldError.getDefaultMessage()
            ));
        }

        ApiResponse<List<FieldErrorDto>> apiResponse = ApiResponse.onFailure(
                ErrorStatus._BAD_REQUEST.getCode(),
                ErrorStatus._BAD_REQUEST.getMessage(),
                fieldErrors);

        return ResponseEntity.status(ErrorStatus._BAD_REQUEST.getHttpStatus()).body(apiResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleException(Exception e) {
        log.error("Unhandled exception occurred", e);  // 스택 트레이스 포함 로깅
        ApiResponse<Object> apiResponse = ApiResponse.onFailure(
                ErrorStatus._INTERNAL_SERVER_ERROR.getCode(),
                ErrorStatus._INTERNAL_SERVER_ERROR.getMessage(),
                null);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
    }

    // 에러 메시지에서 적절한 ErrorStatus를 결정하는 헬퍼 메서드
    private ErrorStatus getErrorStatusFromMessage(String message) {
        if (message.contains("User not found")) {
            return ErrorStatus.USER_NOT_FOUND;
        } else if (message.contains("Course not found")) {
            return ErrorStatus.COURSE_NOT_FOUND;
        } else if (message.contains("Week not found")) {
            return ErrorStatus.WEEK_NOT_FOUND;
        } else if (message.contains("Question not found")) {
            return ErrorStatus.QUESTION_NOT_FOUND;
        } else {
            return ErrorStatus._BAD_REQUEST; // 기본 에러 상태
        }
    }
}