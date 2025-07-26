package com.bos.backend.presentation

import com.bos.backend.application.CustomException
import com.bos.backend.presentation.auth.dto.ErrorResponse
import com.bos.backend.presentation.auth.dto.FieldErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.support.WebExchangeBindException

@ControllerAdvice
class GlobalExceptionHandler {
    companion object {
        private const val REQUIRED_FIELD_MESSAGE = "필수 입력 항목입니다."
        private const val INVALID_EMAIL_MESSAGE = "이메일 형식이 올바르지 않습니다."
        private const val INVALID_PASSWORD_MESSAGE = "비밀번호는 영문, 숫자, 특수문자를 포함한 8자 이상이어야 합니다."
        private const val INVALID_LENGTH_MESSAGE = "길이가 올바르지 않습니다."
        private const val INVALID_MIN_VALUE_MESSAGE = "최소값보다 작습니다."
        private const val INVALID_MAX_VALUE_MESSAGE = "최대값보다 큽니다."
        private const val INVALID_FIELD_MESSAGE = "유효하지 않은 값입니다."
    }

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

    private fun resolveErrorCode(error: FieldError): String =
        when (error.code) {
            "NotBlank" -> "REQUIRED_FIELD"
            "Email" -> "INVALID_EMAIL"
            "ValidPassword" -> "INVALID_PASSWORD_FORMAT"
            "Pattern" -> "INVALID_FORMAT"
            "Size" -> "INVALID_LENGTH"
            "Min" -> "INVALID_MIN_VALUE"
            "Max" -> "INVALID_MAX_VALUE"
            else -> "INVALID_FIELD"
        }

    private fun resolveErrorMessage(error: FieldError): String =
        when (error.code) {
            "NotBlank" -> REQUIRED_FIELD_MESSAGE
            "Email" -> INVALID_EMAIL_MESSAGE
            "ValidPassword" -> INVALID_PASSWORD_MESSAGE
            "Pattern" -> error.defaultMessage ?: INVALID_FIELD_MESSAGE
            "Size" -> INVALID_LENGTH_MESSAGE
            "Min" -> INVALID_MIN_VALUE_MESSAGE
            "Max" -> INVALID_MAX_VALUE_MESSAGE
            else -> INVALID_FIELD_MESSAGE
        }

    // 클라이언트에게 내부 정보 제공을 막기 위해 고정 에러코드(INTERNAL_SERVER_ERROR)만 내려줍니다.
    @Suppress("UNUSED_PARAMETER")
    @ExceptionHandler(Exception::class)
    fun handleUnknownException(e: Exception): ResponseEntity<ErrorResponse> =
        ResponseEntity(
            ErrorResponse(
                errorCode = "INTERNAL_SERVER_ERROR",
                message = "서버 내부 오류가 발생했습니다.",
            ),
            HttpStatus.INTERNAL_SERVER_ERROR,
        )
}
