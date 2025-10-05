package com.bos.backend.infrastructure.util

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class PasswordPolicyTest :
    StringSpec({
        "유효한 비밀번호는 검증을 통과해야 한다" {
            PasswordPolicy.isValidPassword("Password123!") shouldBe true
            PasswordPolicy.isValidPassword("Abc12345!") shouldBe true
            PasswordPolicy.isValidPassword("Test@1234") shouldBe true
            PasswordPolicy.isValidPassword("abcDEF123@") shouldBe true
        }

        "영문자가 없는 비밀번호는 검증을 실패해야 한다" {
            PasswordPolicy.isValidPassword("12345678!@#") shouldBe false
        }

        "숫자가 없는 비밀번호는 검증을 실패해야 한다" {
            PasswordPolicy.isValidPassword("Password!@#") shouldBe false
        }

        "특수문자가 없는 비밀번호는 검증을 실패해야 한다" {
            PasswordPolicy.isValidPassword("Password1234") shouldBe false
        }

        "8자 미만 비밀번호는 검증을 실패해야 한다" {
            PasswordPolicy.isValidPassword("Abc123!") shouldBe false
        }

        "허용되지 않은 특수문자를 포함한 비밀번호는 검증을 실패해야 한다" {
            PasswordPolicy.isValidPassword("Password123^") shouldBe false
            PasswordPolicy.isValidPassword("Test1234()") shouldBe false
        }

        "공백을 포함한 비밀번호는 검증을 실패해야 한다" {
            PasswordPolicy.isValidPassword("Pass word123!") shouldBe false
        }

        "빈 문자열은 검증을 실패해야 한다" {
            PasswordPolicy.isValidPassword("") shouldBe false
        }

        "허용된 모든 특수문자를 사용한 비밀번호는 검증을 통과해야 한다" {
            PasswordPolicy.isValidPassword("Password1@") shouldBe true
            PasswordPolicy.isValidPassword("Password1\$") shouldBe true
            PasswordPolicy.isValidPassword("Password1!") shouldBe true
            PasswordPolicy.isValidPassword("Password1%") shouldBe true
            PasswordPolicy.isValidPassword("Password1*") shouldBe true
            PasswordPolicy.isValidPassword("Password1#") shouldBe true
            PasswordPolicy.isValidPassword("Password1?") shouldBe true
            PasswordPolicy.isValidPassword("Password1&") shouldBe true
        }
    })
