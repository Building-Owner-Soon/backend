package com.bos.backend.presentation.auth.controller

import com.bos.backend.application.auth.AuthService
import com.bos.backend.presentation.auth.dto.CommonSignResponseDTO
import com.bos.backend.presentation.auth.dto.SignInRequestDTO
import com.bos.backend.presentation.auth.dto.SignUpRequestDTO
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthController(
    private val authService: AuthService,
) {
    @PostMapping("/auth/sign-up")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun signUp(
        @RequestBody signUpRequestDTO: SignUpRequestDTO,
    ): CommonSignResponseDTO = authService.signUp(signUpRequestDTO)

    @PostMapping("/auth/sign-in")
    @ResponseStatus(HttpStatus.OK)
    suspend fun signIn(
        @RequestBody signInRequestDTO: SignInRequestDTO,
    ): CommonSignResponseDTO = authService.signIn(signInRequestDTO)
//
//    @GetMapping("/me")
//    @ResponseStatus(HttpStatus.OK)
//    suspend fun getMe(): MeResponse {
//        TODO()
//    }
}
