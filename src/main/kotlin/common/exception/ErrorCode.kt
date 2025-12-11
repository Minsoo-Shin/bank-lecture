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
    CALL_RESULT_BODY_NULL(-100, "body is null")

}