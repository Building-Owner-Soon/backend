package com.bos.backend.presentation.auth.dto.validation

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class PasswordValidator : ConstraintValidator<ValidPassword, String> {
    private lateinit var annotation: ValidPassword

    override fun initialize(constraintAnnotation: ValidPassword) {
        this.annotation = constraintAnnotation
    }

    override fun isValid(
        value: String?,
        context: ConstraintValidatorContext,
    ): Boolean {
        if (value.isNullOrBlank()) return true

        val regex = Regex("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@\$!%*#?&])[A-Za-z\\d@\$!%*#?&]{8,}$")
        val isValid = regex.matches(value)

        if (!isValid) {
            context.disableDefaultConstraintViolation()
            context.buildConstraintViolationWithTemplate(annotation.message)
                .addConstraintViolation()
        }

        return isValid
    }
}
