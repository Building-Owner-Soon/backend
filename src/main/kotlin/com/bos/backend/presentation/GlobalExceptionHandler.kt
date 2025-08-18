package com.bos.backend.presentation

import com.bos.backend.application.CustomException
import com.bos.backend.presentation.auth.dto.ErrorResponse
import com.bos.backend.presentation.auth.dto.FieldErrorResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.support.WebExchangeBindException

@ControllerAdvice
class GlobalExceptionHandler {
    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(CustomException::class)
    fun handleCustomException(e: CustomException): ResponseEntity<ErrorResponse> =
        ResponseEntity(
            ErrorResponse(
                errorCode = e.errorCode,
                message = e.message,
            ),
            e.status,
        )

    @ExceptionHandler(WebExchangeBindException::class)
    fun handleWebExchangeBindException(ex: WebExchangeBindException): ResponseEntity<ErrorResponse> {
        val errors =
            ex.bindingResult.fieldErrors.map {
                FieldErrorResponse(
                    field = it.field,
                    errorCode = resolveErrorCode(it),
                    message = resolveErrorMessage(it),
                )
            }

        return ResponseEntity.badRequest().body(
            ErrorResponse(
                errorCode = "INVALID_REQUEST",
                message = "요청값이 올바르지 않습니다.",
                errors = errors,
            ),
        )
    }

    private fun resolveErrorCode(error: FieldError): String = ConstraintViolationConstant.getErrorCode(error.code ?: "")

    private fun resolveErrorMessage(error: FieldError): String =
        ConstraintViolationConstant.getErrorMessage(error.code ?: "")

    // 클라이언트에게 내부 정보 제공을 막기 위해 고정 에러코드(INTERNAL_SERVER_ERROR)만 내려줍니다.
    @Suppress("UNUSED_PARAMETER")
    @ExceptionHandler(Exception::class)
    fun handleUnknownException(e: Exception): ResponseEntity<ErrorResponse> {
        // TODO: 추후 에러 alert / 메트릭 수집 필요
        logger.error("Unhandled exception occurred", e)

        return ResponseEntity(
            ErrorResponse(
                errorCode = "INTERNAL_SERVER_ERROR",
                message = "서버 내부 오류가 발생했습니다.",
            ),
            HttpStatus.INTERNAL_SERVER_ERROR,
        )
    }
}
