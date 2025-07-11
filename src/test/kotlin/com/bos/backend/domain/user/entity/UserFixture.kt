package com.bos.backend.domain.user.entity

import java.time.Instant

object UserFixture {
    fun defaultUserFixture() =
        User(
            id = 1L,
            nickname = "홍길동",
            profileImageUrl = "https://example/com/image",
            allowNotification = true,
            createdAt = Instant.now(),
            updatedAt = Instant.now(),
            deletedAt = null,
        )
}
