package com.bos.backend.application.auth

import org.springframework.http.HttpStatus

enum class AuthErrorCode(
    val status: HttpStatus,
) {
    EMAIL_DUPLICATE(HttpStatus.CONFLICT),
    EMAIL_VERIFICATION_CODE_MISMATCH(HttpStatus.BAD_REQUEST),
    EMAIL_VERIFICATION_CODE_EXPIRED(HttpStatus.BAD_REQUEST),
} 