package com.bos.backend.application

import org.springframework.http.HttpStatus

class CustomException(
    val errorCode: String,
    val status: HttpStatus = HttpStatus.BAD_REQUEST,
) : RuntimeException()
