package com.example.common.exception

interface CodeInterface {
    val code: Int
    var message: String
}

enum class ErrorCode(
    override val code: Int,
    override var message: String
) : CodeInterface {
    AUTH_CONFIG_NOT_FOUND(-100, "auth config not found"),
    FAILED_TO_CALL_CLIENT(-100, "failed to call client"),
    CALL_RESULT_BODY_NULL(-100, "body is null"),
    PROVIDER_NOT_FOUND(-100, "provider not found"),
    TOKEN_IS_INVALID(-100, "token is invalid"),
    TOKEN_IS_EXPIRED(-100, "token is expired")
}