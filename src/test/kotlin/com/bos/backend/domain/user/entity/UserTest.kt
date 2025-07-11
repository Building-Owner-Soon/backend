package com.bos.backend.domain.user.entity

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.time.Instant

class UserTest :
    StringSpec({
        val sut =
            User(
                id = 1L,
                nickname = "홍길동",
                profileImageUrl = "https://example/com/image",
                allowNotification = true,
                createdAt = Instant.now(),
                updatedAt = Instant.now(),
                deletedAt = null,
            )

        "유저는 초기화 시 삭제되지 않은 상태여야 한다" {
            sut.isDeleted() shouldBe false
        }

        "delete 호출시 삭제된 상태를 확인할 수 있어야 한다" {
            val deletedUser = sut.delete()
            deletedUser.isDeleted() shouldBe true
        }
    })
