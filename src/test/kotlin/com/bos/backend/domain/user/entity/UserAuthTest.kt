package com.bos.backend.domain.user.entity

import com.bos.backend.domain.user.enum.ProviderType
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeTypeOf

class UserAuthTest :
    StringSpec({
        val sut =
            UserAuth(
                id = 1L,
                userId = 1L,
                _providerType = "KAKAO",
                providerId = "123456",
                lastLoginAt = null,
            )
        "providerType 참조시 ProviderType 객체를 반환해야한다" {
            sut.providerType.shouldBeTypeOf<ProviderType>()
            sut.providerType shouldBe ProviderType.KAKAO
        }
        "updateLastLoginAt 호출시 lastLoginAt이 현재 시간으로 업데이트되어야 한다" {
            sut.let { userAuth ->
                userAuth.lastLoginAt shouldNotBe userAuth.updateLastLoginAt().lastLoginAt
            }
        }
    })
