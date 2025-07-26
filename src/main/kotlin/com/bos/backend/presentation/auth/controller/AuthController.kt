package com.bos.backend.presentation.auth.controller

import com.bos.backend.application.auth.AuthService
import com.bos.backend.presentation.auth.dto.CommonSignResponseDTO
import com.bos.backend.presentation.auth.dto.EmailVerificationCheckDTO
import com.bos.backend.presentation.auth.dto.EmailVerificationRequestDTO
import com.bos.backend.presentation.auth.dto.ErrorResponse
import com.bos.backend.presentation.auth.dto.SignInRequestDTO
import com.bos.backend.presentation.auth.dto.SignUpRequestDTO
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
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

    @GetMapping("/auth/check-email")
    @ResponseStatus(HttpStatus.OK)
    @Suppress("SwallowedException")
    suspend fun checkEmail(
        @RequestParam email: String,
    ): ResponseEntity<*> =
        try {
            ResponseEntity.status(HttpStatus.OK).body(authService.isBosEmailUserAbsent(email))
        } catch (e: NoSuchElementException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse("EMAIL_NOT_FOUND"))
        }

    @PostMapping("/auth/email-verification")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun sendVerificationEmail(
        @RequestBody emailVerificationRequestDTO: EmailVerificationRequestDTO,
    ) = authService.sendVerificationEmail(emailVerificationRequestDTO)

    @PostMapping("/auth/email-verification/verify-code")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun verifyEmail(
        @RequestBody request: EmailVerificationCheckDTO,
    ) = authService.verifyEmail(request)
}
