package com.bos.backend.presentation.user.controller

import com.bos.backend.application.user.UserService
import com.bos.backend.presentation.user.dto.UpdateUserRequestDTO
import com.bos.backend.presentation.user.dto.UserProfileDTO
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController(
    private val userService: UserService,
) {
    @GetMapping("/users/me")
    suspend fun getMe(): UserProfileDTO =
        // TODO: 토큰 검증 후 SecurityContext에 인증정보 로직 추가후 반영
        userService.getUserProfile(1L)

    @PatchMapping("/users/me")
    suspend fun patchMe(
        @RequestBody updateUserRequestDTO: UpdateUserRequestDTO,
    ): UserProfileDTO =
        // TODO: 토큰 검증 후 SecurityContext에 인증정보 로직 추가후 반영
        userService.updateUserProfile(
            userId = 1L,
            updateUserRequestDTO = updateUserRequestDTO,
        )
}
