package com.bos.backend.domain.user.entity

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class UserAuthProviderTest :
    StringSpec({
        val sut =
            UserAuthProvider(
                id = 1L,
                name = "KAKAO",
            )

        "equals 호출시 동일한 id를 가진 UserAuthProvider는 동일한 객체로 인식되어야 한다" {
            val sameSut = UserAuthProvider(id = 1L, name = "KAKAO")
            sut shouldBe sameSut
        }

        "equals 호출시 다른 id를 가진 UserAuthProvider는 동일한 객체로 인식되지 않아야 한다" {
            val differentSut = UserAuthProvider(id = 2L, name = "KAKAO")
            sut shouldBe differentSut
        }
    })
