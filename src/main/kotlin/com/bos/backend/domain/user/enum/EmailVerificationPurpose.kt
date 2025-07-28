package com.bos.backend.domain.user.enum

enum class EmailVerificationPurpose(val value: String) {
    SIGNUP("signup"),
    PASSWORD_RESET("password-reset"),
}
