package com.bos.backend.presentation.auth.dto

data class ErrorResponse(
    val errorCode: String,
    val message: String,
    val errors: List<FieldErrorResponse> = emptyList(),
)

data class FieldErrorResponse(
    val field: String,
    val errorCode: String,
    val message: String,
)
