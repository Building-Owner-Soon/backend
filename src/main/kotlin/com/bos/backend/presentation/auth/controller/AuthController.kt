package com.bos.backend.presentation.auth.controller

import com.bos.backend.application.auth.AuthService
import com.bos.backend.presentation.auth.dto.CommonSignResponseDTO
import com.bos.backend.presentation.auth.dto.EmailVerificationCodeRequestDTO
import com.bos.backend.presentation.auth.dto.EmailVerificationRequestDTO
import com.bos.backend.presentation.auth.dto.PasswordResetRequestDTO
import com.bos.backend.presentation.auth.dto.SignInRequestDTO
import com.bos.backend.presentation.auth.dto.SignUpRequestDTO
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
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

    @PostMapping("/auth/email-verification")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun requestEmailVerification(
        @RequestBody emailVerificationRequestDTO: EmailVerificationRequestDTO,
    ): ResponseEntity<Unit> {
        if (emailVerificationRequestDTO.email.contains("exceed")) {
            return ResponseEntity
                .of(
                    ProblemDetail
                        .forStatus(HttpStatus.BAD_REQUEST)
                        .apply {
                            title = "Request limit exceeded"
                            properties = mapOf("errorCode" to "REQUEST_LIMIT_EXCEEDED")
                        },
                ).build()
        }
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/auth/email-verification/verify-code")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun verifyEmailCode(
        @RequestBody emailVerificationCodeRequestDTO: EmailVerificationCodeRequestDTO,
    ): ResponseEntity<Unit> =
        when (emailVerificationCodeRequestDTO.code) {
            "000000" -> {
                ResponseEntity.noContent().build()
            }
            else -> {
                // 만료
                // throw IllegalArgumentException("Expired verification code")
                ResponseEntity
                    .of(
                        ProblemDetail
                            .forStatus(HttpStatus.BAD_REQUEST)
                            .apply {
                                title = "Expired verification code"
                                properties = mapOf("errorCode" to "EXPIRED_VERIFICATION_CODE")
                            },
                    ).build()
            }
        }

    @PostMapping("/auth/password-reset")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Suppress("ReturnCount")
    suspend fun requestResetPassword(passwordResetRequestDTO: PasswordResetRequestDTO): ResponseEntity<Unit> {
        if (passwordResetRequestDTO.email.contains("exceed")) {
            return ResponseEntity
                .of(
                    ProblemDetail
                        .forStatus(HttpStatus.BAD_REQUEST)
                        .apply {
                            title = "Request limit exceeded"
                            properties = mapOf("errorCode" to "REQUEST_LIMIT_EXCEEDED")
                        },
                ).build()
        } else if (passwordResetRequestDTO.email.contains("kakao")) {
            return ResponseEntity
                .of(
                    ProblemDetail
                        .forStatus(HttpStatus.BAD_REQUEST)
                        .apply {
                            title = "Invalid auth provider"
                            properties = mapOf("errorCode" to "INVALID_AUTH_PROVIDER")
                        },
                ).build()
        }
        return ResponseEntity.noContent().build()
    }
}
