package com.bos.backend.domain.user.entity

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldNotBe

class UserAuthTest :
    StringSpec({
        val sut =
            UserAuth(
                id = 1L,
                userId = 1L,
                providerId = "123456",
                userProviderId = 1234567890L,
                lastLoginAt = null,
            )

        "updateLastLoginAt 호출시 lastLoginAt이 현재 시간으로 업데이트되어야 한다" {
            sut.let { userAuth ->
                userAuth.lastLoginAt shouldNotBe userAuth.updateLastLoginAt().lastLoginAt
            }
        }
    })
