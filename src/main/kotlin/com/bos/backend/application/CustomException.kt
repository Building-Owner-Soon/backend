package com.bos.backend.application

import org.springframework.http.HttpStatus

interface ErrorCode {
    val message: String
    val status: HttpStatus
}

class CustomException(
    val errorCode: String,
    override val message: String,
    val status: HttpStatus = HttpStatus.BAD_REQUEST,
) : RuntimeException() {
    constructor(errorCode: Enum<*>, message: String, status: HttpStatus) : this(
        errorCode = errorCode.name,
        message = message,
        status = status,
    )

    constructor(errorCode: ErrorCode) : this(
        errorCode = errorCode::class.simpleName + "." + (errorCode as Enum<*>).name,
        message = errorCode.message,
        status = errorCode.status,
    )
}

enum class CommonErrorCode(
    override val message: String,
    override val status: HttpStatus = HttpStatus.BAD_REQUEST,
) : ErrorCode {
    INVALID_PARAMETER("유효하지 않은 파라미터입니다."),
}
