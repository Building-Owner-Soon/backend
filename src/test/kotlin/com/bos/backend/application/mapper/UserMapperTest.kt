package com.bos.backend.application.mapper

import com.bos.backend.domain.user.entity.Character
import com.bos.backend.domain.user.entity.CharacterAsset
import com.bos.backend.domain.user.entity.User
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.net.URI
import java.time.Instant

class UserMapperTest :
    StringSpec({

        val userMapper = UserMapper.INSTANCE

        "User를 UserProfileDTO로 매핑할 수 있어야 한다" {
            // given
            val user =
                User(
                    id = 1L,
                    nickname = "테스트유저",
                    isNotificationAllowed = true,
                    isMarketingAgreed = false,
                    character =
                        Character(
                            faceShape = CharacterAsset("face_1", URI.create("https://example.com/face.svg")),
                            hand = CharacterAsset("hand_1", URI.create("https://example.com/hand.svg")),
                            skinColor = "#FFDBAC",
                            frontHair =
                                CharacterAsset(
                                    "front_hair_1",
                                    URI.create("https://example.com/front_hair.svg"),
                                ),
                            backHair =
                                CharacterAsset(
                                    "back_hair_1",
                                    URI.create("https://example.com/back_hair.svg"),
                                ),
                            eyes = CharacterAsset("eyes_1", URI.create("https://example.com/eyes.svg")),
                            mouth = CharacterAsset("mouth_1", URI.create("https://example.com/mouth.svg")),
                        ),
                    homeType = "APARTMENT",
                    createdAt = Instant.now(),
                    updatedAt = Instant.now(),
                )

            // when
            val result = userMapper.toUserProfileDTO(user)

            // then
            result.id shouldBe 1L
            result.nickname shouldBe "테스트유저"
            result.isNotificationAllowed shouldBe true
            result.isMarketingAgreed shouldBe false
            result.character shouldNotBe null
            result.character?.faceShape?.id shouldBe "face_1"
            result.homeType shouldBe "APARTMENT"
            result.createdAt shouldBe user.createdAt
            result.updatedAt shouldBe user.updatedAt
        }

        "character가 null인 User도 매핑할 수 있어야 한다" {
            // given
            val user =
                User(
                    id = 2L,
                    nickname = "캐릭터없는유저",
                    isNotificationAllowed = false,
                    isMarketingAgreed = true,
                    character = null,
                    homeType = null,
                    createdAt = Instant.now(),
                    updatedAt = Instant.now(),
                )

            // when
            val result = userMapper.toUserProfileDTO(user)

            // then
            result.id shouldBe 2L
            result.nickname shouldBe "캐릭터없는유저"
            result.character shouldBe null
            result.homeType shouldBe null
        }
    })
