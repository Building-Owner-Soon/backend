package com.bos.backend.infrastructure.database

import com.bos.backend.domain.user.entity.CharacterAsset
import com.bos.backend.domain.user.entity.CharacterComponents
import com.bos.backend.domain.user.entity.User
import com.bos.backend.domain.user.repository.UserRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.MariaDBContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.net.URI

@Testcontainers
@SpringBootTest
class R2dbcEntityMappingIntegrationTest(
    private val userRepository: UserRepository,
) : StringSpec({

        "User 엔티티를 데이터베이스에 저장하고 조회할 수 있어야 한다" {
            val characterComponents =
                CharacterComponents(
                    faceShape = CharacterAsset("face_1", URI("https://example.com/face1.png")),
                    hand = CharacterAsset("hand_1", URI("https://example.com/hand1.png")),
                    skinColor = "#FFE4C4",
                    frontHair = CharacterAsset("front_hair_1", URI("https://example.com/front_hair1.png")),
                    backHair = CharacterAsset("back_hair_1", URI("https://example.com/back_hair1.png")),
                    eyes = CharacterAsset("eyes_1", URI("https://example.com/eyes1.png")),
                    mouth = CharacterAsset("mouth_1", URI("https://example.com/mouth1.png")),
                )

            val user =
                User(
                    nickname = "테스트사용자",
                    isNotificationAllowed = true,
                    isMarketingAgreed = false,
                    characterComponents = characterComponents,
                    homeType = "APARTMENT",
                )

            val savedUser = userRepository.save(user)

            savedUser.id shouldNotBe null
            savedUser.nickname shouldBe "테스트사용자"
            savedUser.isNotificationAllowed shouldBe true
            savedUser.isMarketingAgreed shouldBe false
            savedUser.homeType shouldBe "APARTMENT"
            savedUser.characterComponents shouldNotBe null
            savedUser.characterComponents?.faceShape?.key shouldBe "face_1"
            savedUser.createdAt shouldNotBe null
            savedUser.updatedAt shouldNotBe null
            savedUser.deletedAt shouldBe null
        }

        "저장된 User 엔티티를 ID로 조회할 수 있어야 한다" {
            // Given
            val user =
                User(
                    nickname = "조회테스트",
                    isNotificationAllowed = false,
                    isMarketingAgreed = true,
                )
            val savedUser = userRepository.save(user)

            val foundUser = userRepository.findById(savedUser.id!!)

            foundUser shouldNotBe null
            foundUser!!.id shouldBe savedUser.id
            foundUser.nickname shouldBe "조회테스트"
            foundUser.isNotificationAllowed shouldBe false
            foundUser.isMarketingAgreed shouldBe true
        }

        "JSON 필드(characterComponents)가 올바르게 직렬화/역직렬화되어야 한다" {
            // Given
            val complexCharacterComponents =
                CharacterComponents(
                    faceShape =
                        CharacterAsset(
                            "complex_face",
                            URI("https://cdn.example.com/assets/faces/complex_face_v2.svg"),
                        ),
                    hand =
                        CharacterAsset(
                            "hand_with_special_chars",
                            URI("https://cdn.example.com/assets/hands/hand-특수문자-테스트.png"),
                        ),
                    skinColor = "#8B4513",
                    frontHair = CharacterAsset("curly_hair", URI("https://example.com/hair?style=curly&color=brown")),
                    backHair =
                        CharacterAsset(
                            "long_back_hair",
                            URI("https://example.com/hair?style=long&position=back"),
                        ),
                    eyes = CharacterAsset("blue_eyes", URI("https://example.com/eyes/blue_sparkle.gif")),
                    mouth = CharacterAsset("smile_mouth", URI("https://example.com/mouths/happy_smile.webp")),
                )

            val user =
                User(
                    nickname = "JSON테스트",
                    characterComponents = complexCharacterComponents,
                )

            val savedUser = userRepository.save(user)
            val retrievedUser = userRepository.findById(savedUser.id!!)

            retrievedUser shouldNotBe null
            val retrievedComponents = retrievedUser!!.characterComponents
            retrievedComponents shouldNotBe null

            retrievedComponents!!.faceShape.key shouldBe "complex_face"
            retrievedComponents.faceShape.uri shouldBe URI("https://cdn.example.com/assets/faces/complex_face_v2.svg")
            retrievedComponents.hand.key shouldBe "hand_with_special_chars"
            retrievedComponents.hand.uri shouldBe URI("https://cdn.example.com/assets/hands/hand-특수문자-테스트.png")
            retrievedComponents.skinColor shouldBe "#8B4513"
            retrievedComponents.eyes.uri shouldBe URI("https://example.com/eyes/blue_sparkle.gif")
        }

//    "모든 사용자를 조회할 수 있어야 한다" {
//        // Given
//        val users = listOf(
//            User(nickname = "사용자1"),
//            User(nickname = "사용자2"),
//            User(nickname = "사용자3")
//        )
//
//        users.forEach { userRepository.save(it) }
//
//
//        val allUsers = userRepository.findAll().toList()
//
//
//        allUsers.size shouldBe users.size
//        allUsers.map { it.nickname } shouldBe users.map { it.nickname }
//    }

        "User 업데이트가 정상적으로 동작해야 한다" {
            // Given
            val originalUser = User(nickname = "원본사용자", isNotificationAllowed = false)
            val savedUser = userRepository.save(originalUser)

            val updatedUser =
                savedUser.update(
                    nickname = "업데이트된사용자",
                    isNotificationAllowed = true,
                    isMarketingAgreed = null,
                    characterComponents = null,
                    homeType = "VILLA",
                )
            val finalUser = userRepository.save(updatedUser)

            finalUser.id shouldBe savedUser.id
            finalUser.nickname shouldBe "업데이트된사용자"
            finalUser.isNotificationAllowed shouldBe true
            finalUser.homeType shouldBe "VILLA"
            finalUser.updatedAt shouldNotBe savedUser.updatedAt
        }

        "User 소프트 삭제가 정상적으로 동작해야 한다" {
            // Given
            val user = User(nickname = "삭제될사용자")
            val savedUser = userRepository.save(user)

            val deletedUser = savedUser.delete()
            val finalUser = userRepository.save(deletedUser)

            finalUser.isDeleted() shouldBe true
            finalUser.deletedAt shouldNotBe null
            finalUser.updatedAt shouldNotBe savedUser.updatedAt
        }
    }) {
    companion object {
        @Container
        @JvmStatic
        val mariadb =
            MariaDBContainer("mariadb:10.11")
                .withDatabaseName("bos_integration_test")
                .withUsername("testuser")
                .withPassword("testpass")

        @DynamicPropertySource
        @JvmStatic
        fun configureProperties(registry: DynamicPropertyRegistry) {
            // R2DBC 설정
            registry.add("spring.r2dbc.url") {
                "r2dbc:mariadb://${mariadb.host}:${mariadb.firstMappedPort}/${mariadb.databaseName}"
            }
            registry.add("spring.r2dbc.username") { mariadb.username }
            registry.add("spring.r2dbc.password") { mariadb.password }

            // JDBC 설정 (Flyway용)
            registry.add("spring.datasource.url") { mariadb.jdbcUrl }
            registry.add("spring.datasource.username") { mariadb.username }
            registry.add("spring.datasource.password") { mariadb.password }

            // Flyway 활성화 (테스트용 데이터베이스 스키마 생성)
            registry.add("spring.flyway.enabled") { "true" }

            // 기타 테스트 설정
            registry.add("spring.flyway.clean-disabled") { "false" }
        }
    }
}
