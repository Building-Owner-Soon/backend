package com.bos.backend.infrastructure

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.springframework.http.HttpStatus
import org.springframework.mock.http.server.reactive.MockServerHttpRequest

class GlobalExceptionHandlerTest :
    StringSpec({
        val sut = GlobalExceptionHandler()

        val mockServerHttpRequest =
            MockServerHttpRequest
                .get("/test")
                .build()

        "IllegalArguementException이 raise되면 400 Bad Request 응답을 반환한다" {
            val exception = IllegalArgumentException("error detail message")

            sut
                .handleIllegalArgumentException(exception, mockServerHttpRequest)
                .body
                .shouldNotBeNull {
                    title shouldBe "Invalid argument"
                    status shouldBe HttpStatus.BAD_REQUEST.value()
                    detail shouldBe "error detail message"
                }
        }

        "IllegalStateException이 raise되면 409 Conflict 응답을 반환한다" {
            val exception = IllegalStateException("error detail message")

            sut
                .handleIllegalStateException(exception, mockServerHttpRequest)
                .body
                .shouldNotBeNull {
                    title shouldBe "Illegal state error"
                    status shouldBe HttpStatus.CONFLICT.value()
                    detail shouldBe "error detail message"
                }
        }
    })
