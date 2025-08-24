package com.bos.backend.domain.auth.enums

enum class EmailVerificationType(
    val value: String,
) {
    SIGNUP("signup"),
    PASSWORD_RESET("password-reset"),
}
