package com.bos.playground

import io.kotest.core.spec.style.StringSpec
import java.security.SecureRandom
import java.util.Base64

class PlaygroundTest :
    StringSpec({
        "generate jwt key" {
            fun generateHS256SecretKey(): String {
                val keyBytes = ByteArray(32) // 256비트
                SecureRandom().nextBytes(keyBytes)
                return Base64.getEncoder().withoutPadding().encodeToString(keyBytes)
            }

            println("HS256 Secret Key (Base64): ${generateHS256SecretKey()}")
            println("HS256 Secret Key (Base64): ${generateHS256SecretKey()}")
            println("HS256 Secret Key (Base64): ${generateHS256SecretKey()}")
            println("HS256 Secret Key (Base64): ${generateHS256SecretKey()}")
            println("HS256 Secret Key (Base64): ${generateHS256SecretKey()}")
            println("HS256 Secret Key (Base64): ${generateHS256SecretKey()}")
        }
    })
