package com.bos.backend.presentation.auth.dto.validation

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.annotation.AnnotationTarget.FIELD
import kotlin.annotation.AnnotationTarget.PROPERTY_GETTER
import kotlin.reflect.KClass

@Target(FIELD, PROPERTY_GETTER)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [PasswordValidator::class])
annotation class ValidPassword(
    val message: String = "비밀번호는 영문, 숫자, 특수문자를 포함한 8자 이상이어야 합니다.",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
)
