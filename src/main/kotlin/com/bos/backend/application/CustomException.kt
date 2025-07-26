package com.bos.backend.application

import com.bos.backend.application.auth.AuthErrorCode
import org.springframework.http.HttpStatus

class CustomException(
    val errorCode: String,
    override val message: String,
    val status: HttpStatus = HttpStatus.BAD_REQUEST,
) : RuntimeException() {
    
    constructor(authErrorCode: AuthErrorCode) : this(
        errorCode = authErrorCode.name,
        message = authErrorCode.message,
        status = authErrorCode.status,
    )
}
