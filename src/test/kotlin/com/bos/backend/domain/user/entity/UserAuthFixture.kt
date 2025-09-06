package com.bos.backend.domain.user.entity

object UserAuthFixture {
    fun defaultUserAuthFixture(): UserAuth =
        UserAuth(
            id = 1L,
            userId = 1L,
            _providerType = "KAKAO",
            providerId = "123456",
            email = "abc@abc.com",
            lastLoginAt = null,
        )
}
