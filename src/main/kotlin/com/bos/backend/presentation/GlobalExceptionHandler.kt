package com.bos.backend.presentation.exception

import com.bos.backend.presentation.auth.dto.ErrorResponse
import com.bos.backend.application.exception.CustomException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(CustomException::class)
    fun handleCustomException(e: CustomException): ResponseEntity<ErrorResponse> =
        ResponseEntity(
            ErrorResponse(
                errorCode = e.errorCode
            ),
            e.status
        )

    // 클라이언트에게 내부 정보 제공을 막기 위해 고정 에러코드(INTERNAL_SERVER_ERROR)만 내려줍니다.
    @Suppress("UNUSED_PARAMETER")
    @ExceptionHandler(Exception::class)
    fun handleUnknownException(e: Exception): ResponseEntity<ErrorResponse> =
        ResponseEntity(
            ErrorResponse(errorCode = "INTERNAL_SERVER_ERROR"),
            HttpStatus.INTERNAL_SERVER_ERROR
        )
} 