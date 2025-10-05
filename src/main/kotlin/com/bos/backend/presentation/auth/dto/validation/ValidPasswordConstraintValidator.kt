package com.bos.backend.presentation.auth.dto.validation

import com.bos.backend.infrastructure.util.PasswordPolicy
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class ValidPasswordConstraintValidator : ConstraintValidator<ValidPassword, String> {
    private lateinit var annotation: ValidPassword

    override fun initialize(constraintAnnotation: ValidPassword) {
        this.annotation = constraintAnnotation
    }

    override fun isValid(
        value: String?,
        context: ConstraintValidatorContext,
    ): Boolean {
        if (value.isNullOrBlank()) return true

        val isValid = PasswordPolicy.isValidPassword(value)

        if (!isValid) {
            context.disableDefaultConstraintViolation()
            context.buildConstraintViolationWithTemplate(annotation.message)
                .addConstraintViolation()
        }

        return isValid
    }
}
