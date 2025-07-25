package com.bos.backend.application.exception

import org.springframework.http.HttpStatus

class CustomException(
    val errorCode: String,
    val status: HttpStatus = HttpStatus.BAD_REQUEST
) : RuntimeException() 