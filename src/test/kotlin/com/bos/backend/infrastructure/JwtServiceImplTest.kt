package com.bos.backend.infrastructure

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class JwtServiceImplTest :
    StringSpec({
        val testKey = "pnu+dnZi2sk3vmtHHt956FDp0dacg5KgzZPb6hRukEo"
        val sut = JwtServiceImpl(testKey)

        "generateToken은 userId와 expirationSeconds를 받아 토큰을 생성한다" {
            val userId = "12345"
            val expirationSeconds = 3600L

            val token = sut.generateToken(userId, expirationSeconds)

            token shouldNotBe null
            sut.validateToken(token) shouldBe true
        }

        "validateToken은 유효한 토큰에 대해 true를 반환한다" {
            val token = sut.generateToken("12345", 3600L)

            sut.validateToken(token) shouldBe true
        }

        "validateToken은 유효하지 않은 토큰이 입력되었을 때, false를 반환한다" {
            val invalidToken = "invalid.token.value"

            sut.validateToken(invalidToken) shouldBe false
        }

        "getUserIdFromToken" {
            val token = sut.generateToken("12345", 3600L)

            sut.getUserIdFromToken(token) shouldBe 12345L
        }
    })
