package com.bos.backend.domain.user.enum

enum class AuthProviderType(
    val value: String,
) {
    KAKAO("KAKAO"),
    BOS("BOS"),
    ;

    companion object {
        fun fromValue(value: String): AuthProviderType =
            entries.find { it.value == value }
                ?: throw IllegalArgumentException("Unknown provider: $value")
    }
}
