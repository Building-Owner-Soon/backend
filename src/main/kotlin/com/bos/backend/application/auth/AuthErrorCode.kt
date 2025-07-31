package com.bos.backend.application.auth

import com.bos.backend.application.ErrorCode
import org.springframework.http.HttpStatus

enum class AuthErrorCode(
    override val message: String,
    override val status: HttpStatus,
) : ErrorCode {
    EMAIL_DUPLICATE(
        "이미 존재하는 이메일입니다.",
        HttpStatus.CONFLICT,
    ),
    EMAIL_VERIFICATION_CODE_MISMATCH(
        "인증 코드가 올바르지 않습니다.",
        HttpStatus.BAD_REQUEST,
    ),
    EMAIL_VERIFICATION_CODE_EXPIRED(
        "인증 코드가 만료되었습니다.",
        HttpStatus.BAD_REQUEST,
    ),
    PASSWORD_POLICY_VIOLATION(
        "비밀번호는 영문, 숫자, 특수문자를 포함한 8자 이상이어야 합니다.",
        HttpStatus.BAD_REQUEST,
    ),
    USER_NOT_FOUND(
        "사용자를 찾을 수 없습니다.",
        HttpStatus.BAD_REQUEST,
    ),
    INVALID_TOKEN(
        "유효하지 않은 토큰입니다.",
        HttpStatus.UNAUTHORIZED,
    ),
}
