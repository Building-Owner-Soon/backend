package com.bos.backend.presentation

object ConstraintViolationConstant {
    val ERROR_CODE =
        mapOf(
            "NotBlank" to "REQUIRED_FIELD",
            "Email" to "INVALID_EMAIL",
            "ValidPassword" to "INVALID_PASSWORD_FORMAT",
            "Pattern" to "INVALID_FORMAT",
            "Size" to "INVALID_LENGTH",
            "Min" to "INVALID_MIN_VALUE",
            "Max" to "INVALID_MAX_VALUE",
        )

    val ERROR_MESSAGE =
        mapOf(
            "NotBlank" to "필수 입력 항목입니다.",
            "Email" to "이메일 형식이 올바르지 않습니다.",
            "ValidPassword" to "비밀번호는 영문, 숫자, 특수문자를 포함한 8자 이상이어야 합니다.",
            "Pattern" to "유효하지 않은 값입니다.",
            "Size" to "길이가 올바르지 않습니다.",
            "Min" to "최소값보다 작습니다.",
            "Max" to "최대값보다 큽니다.",
        )

    const val DEFAULT_ERROR_CODE = "INVALID_FIELD"
    const val DEFAULT_ERROR_MESSAGE = "유효하지 않은 값입니다."

    fun getErrorCode(validationCode: String): String {
        return ERROR_CODE[validationCode] ?: DEFAULT_ERROR_CODE
    }

    fun getErrorMessage(validationCode: String): String {
        return ERROR_MESSAGE[validationCode] ?: DEFAULT_ERROR_MESSAGE
    }
}
