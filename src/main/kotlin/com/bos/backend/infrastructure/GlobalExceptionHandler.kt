package com.bos.backend.infrastructure

import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.net.URI

@ControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(IllegalArgumentException::class)
    suspend fun handleIllegalArgumentException(
        ex: IllegalArgumentException,
        request: ServerHttpRequest,
    ): ResponseEntity<ProblemDetail> {
        val httpStatus = HttpStatus.BAD_REQUEST
        val problemDetail =
            ProblemDetail
                .forStatus(httpStatus)
                .apply {
                    title = "Invalid argument"
                    detail = ex.message ?: "Error occurred without a specific message"
                    instance = URI.create(request.path.toString())
                }
        return ResponseEntity.status(httpStatus).body(problemDetail)
    }

    @ExceptionHandler(IllegalStateException::class)
    suspend fun handleIllegalStateException(
        ex: IllegalStateException,
        request: ServerHttpRequest,
    ): ResponseEntity<ProblemDetail> {
        val httpStatus = HttpStatus.CONFLICT
        val problemDetail =
            ProblemDetail
                .forStatus(httpStatus)
                .apply {
                    title = "Illegal state error"
                    detail = ex.message ?: "Error occurred without a specific message"
                    instance = URI.create(request.path.toString())
                }
        return ResponseEntity.status(httpStatus).body(problemDetail)
    }

    @ExceptionHandler(NoSuchElementException::class)
    suspend fun handleNoSuchElementException(
        ex: NoSuchElementException,
        request: ServerHttpRequest,
    ): ResponseEntity<ProblemDetail> {
        val httpStatus = HttpStatus.NOT_FOUND
        val problemDetail =
            ProblemDetail
                .forStatus(httpStatus)
                .apply {
                    title = "Not Exist Element"
                    detail = ex.message ?: "Error occurred without a specific message"
                    instance = URI.create(request.path.toString())
                }
        return ResponseEntity.status(httpStatus).body(problemDetail)
    }
}
