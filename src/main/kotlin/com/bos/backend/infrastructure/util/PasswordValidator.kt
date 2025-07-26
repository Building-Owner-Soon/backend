package com.bos.backend.infrastructure.util

object PasswordValidator {
    fun isValidPassword(password: String): Boolean {
        val hasUppercase = password.any { it.isUpperCase() }
        val hasLowercase = password.any { it.isLowerCase() }
        val hasDigit = password.any { it.isDigit() }
        val hasSpecialChar = password.any { !it.isLetterOrDigit() }
        val isLongEnough = password.length >= 8

        return hasUppercase && hasLowercase && hasDigit && hasSpecialChar && isLongEnough
    }
}
