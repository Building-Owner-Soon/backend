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
    constructor(errorCode: ErrorCode) : this(
        errorCode = (errorCode as Enum<*>).name,
        message = errorCode.message,
        status = errorCode.status,
    )
}

enum class CommonErrorCode(
    override val message: String,
    override val status: HttpStatus = HttpStatus.BAD_REQUEST,
) : ErrorCode {
    TOO_MANY_REQUESTS(
        "요청이 너무 많습니다. 잠시 후 다시 시도해주세요.",
        HttpStatus.TOO_MANY_REQUESTS,
    ),
    INVALID_PARAMETER(
        "잘못된 요청입니다.",
        HttpStatus.BAD_REQUEST,
    ),
}
