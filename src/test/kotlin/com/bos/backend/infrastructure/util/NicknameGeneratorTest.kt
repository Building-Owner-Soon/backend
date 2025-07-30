package com.bos.backend.infrastructure.util

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class NicknameGeneratorTest : DescribeSpec({

    describe("NicknameGenerator") {
        context("generateRandomNickname") {
            it("should generate a non-empty nickname") {
                val nickname = NicknameGenerator.generateRandomNickname()
                nickname shouldNotBe ""
                nickname.length shouldNotBe 0
            }

            it("should generate different nicknames on multiple calls") {
                val nickname1 = NicknameGenerator.generateRandomNickname()
                val nickname2 = NicknameGenerator.generateRandomNickname()

                // While it's theoretically possible they could be the same due to randomness,
                // it's highly unlikely with our current implementation
                // This test might rarely fail due to random chance, but it's acceptable
                nickname1 shouldNotBe nickname2
            }

            it("should contain Korean characters") {
                val nickname = NicknameGenerator.generateRandomNickname()
                // Should contain Korean characters (한글)
                val containsKorean = nickname.any { it in '\uAC00'..'\uD7AF' }
                containsKorean shouldBe true
            }

            it("should contain numbers") {
                val nickname = NicknameGenerator.generateRandomNickname()
                val containsDigit = nickname.any { it.isDigit() }
                containsDigit shouldBe true
            }
        }
    }
})
