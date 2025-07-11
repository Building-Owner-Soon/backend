package com.bos.backend.presentation.auth.controller

import com.bos.backend.presentation.auth.dto.CommonSignResponseDTO
import com.bos.backend.presentation.auth.dto.MeResponse
import com.bos.backend.presentation.auth.dto.SignInRequestDTO
import com.bos.backend.presentation.auth.dto.SignUpRequestDTO
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthController {
    @PostMapping("/auth/sign-up")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun signUp(signUpRequestDTO: SignUpRequestDTO): CommonSignResponseDTO {
        TODO()
    }

    @PostMapping("/auth/sign-in")
    @ResponseStatus(HttpStatus.OK)
    suspend fun signIn(signInRequestDTO: SignInRequestDTO): CommonSignResponseDTO {
        TODO()
    }

    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    suspend fun getMe(): MeResponse {
        TODO()
    }
}
