package com.bos.backend.application.user

import com.bos.backend.application.CustomException
import com.bos.backend.application.auth.AuthErrorCode
import com.bos.backend.application.mapper.CharacterMapper
import com.bos.backend.application.mapper.UserMapper
import com.bos.backend.application.service.CharacterAssetService
import com.bos.backend.domain.user.entity.UserFixture
import com.bos.backend.domain.user.repository.UserRepository
import com.bos.backend.presentation.user.dto.UpdateUserRequestDTO
import com.bos.backend.presentation.user.dto.UserProfileDTO
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.reactive.executeAndAwait

class UserServiceTest :
    DescribeSpec({

        val userRepository = mockk<UserRepository>()
        val transactionalOperator = mockk<TransactionalOperator>()
        val userMapper = mockk<UserMapper>()
        val characterMapper = mockk<CharacterMapper>()

        val characterAssetService = mockk<CharacterAssetService>()

        val userService =
            UserService(
                userRepository = userRepository,
                transactionalOperator = transactionalOperator,
                userMapper = userMapper,
                characterMapper = characterMapper,
                characterAssetService = characterAssetService,
            )

        describe("getUserProfile") {
            context("존재하는 유저를 조회할 때") {
                it("유저 프로필을 성공적으로 반환한다") {
                    // given
                    val userId = 1L
                    val testUser = UserFixture.defaultUserFixture()
                    val expectedProfileDTO =
                        UserProfileDTO(
                            id = testUser.id!!,
                            nickname = testUser.nickname!!,
                            character = testUser.character,
                            homeType = testUser.homeType,
                            isNotificationAllowed = testUser.isNotificationAllowed,
                            isMarketingAgreed = testUser.isMarketingAgreed,
                            createdAt = testUser.createdAt,
                            updatedAt = testUser.updatedAt,
                        )

                    coEvery { userRepository.findById(userId) } returns testUser
                    every { userMapper.toUserProfileDTO(testUser) } returns expectedProfileDTO

                    // when
                    val result = userService.getUserProfile(userId)

                    // then
                    result shouldBe expectedProfileDTO
                    coVerify { userRepository.findById(userId) }
                    verify { userMapper.toUserProfileDTO(testUser) }
                }
            }

            context("존재하지 않는 유저를 조회할 때") {
                it("CustomException을 발생시킨다") {
                    // given
                    val userId = 999L
                    coEvery { userRepository.findById(userId) } returns null

                    // when & then
                    shouldThrow<CustomException> {
                        userService.getUserProfile(userId)
                    }.errorCode shouldBe AuthErrorCode.USER_NOT_FOUND

                    coVerify { userRepository.findById(userId) }
                }
            }
        }

        // updateUserProfile 테스트는 TransactionalOperator 모킹 복잡성으로 인해 통합 테스트에서 처리
        describe("updateUserProfile") {
            context("존재하지 않는 유저를 업데이트하려는 경우") {
                it("NoSuchElementException이 발생해야 한다") {
                    // given
                    val userId = 999L
                    val updateRequest = UpdateUserRequestDTO(nickname = "변경된닉네임")
                    coEvery { userRepository.findById(userId) } returns null
                    coEvery { transactionalOperator.executeAndAwait<UserProfileDTO>(any()) } coAnswers {
                        firstArg<suspend () -> UserProfileDTO>().invoke()
                    }

                    // when & then
                    shouldThrow<NoSuchElementException> {
                        userService.updateUserProfile(userId, updateRequest)
                    }.message shouldBe "User with ID $userId not found"

                    coVerify { userRepository.findById(userId) }
                }
            }
        }
    })
