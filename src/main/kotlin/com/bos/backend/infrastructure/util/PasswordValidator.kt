package com.bos.backend.infrastructure.util

object PasswordValidator {
    private const val MIN_PASSWORD_LENGTH = 8

    fun isValidPassword(password: String): Boolean {
        val hasUppercase = password.any { it.isUpperCase() }
        val hasLowercase = password.any { it.isLowerCase() }
        val hasDigit = password.any { it.isDigit() }
        val hasSpecialChar = password.any { !it.isLetterOrDigit() }
        val isLongEnough = password.length >= MIN_PASSWORD_LENGTH

        return hasUppercase && hasLowercase && hasDigit && hasSpecialChar && isLongEnough
    }
}
