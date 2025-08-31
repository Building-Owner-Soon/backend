package com.bos.backend.presentation.user.controller

import com.bos.backend.application.user.UserService
import com.bos.backend.infrastructure.util.SecurityUtils
import com.bos.backend.presentation.user.dto.UpdateUserRequestDTO
import com.bos.backend.presentation.user.dto.UserProfileResponseDTO
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController(
    private val userService: UserService,
) {
    @GetMapping("/users/me")
    suspend fun getMe(): UserProfileResponseDTO {
        val userId = SecurityUtils.getCurrentUserId()
        return userService.getUserProfile(userId)
    }

    @PatchMapping("/users/me")
    suspend fun patchMe(
        @RequestBody updateUserRequestDTO: UpdateUserRequestDTO,
    ): UserProfileResponseDTO {
        val userId = SecurityUtils.getCurrentUserId()
        return userService.updateUserProfile(
            userId = userId,
            updateUserRequestDTO = updateUserRequestDTO,
        )
    }
}
