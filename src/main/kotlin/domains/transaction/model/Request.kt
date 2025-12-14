package com.example.domains.transaction.model

import jakarta.validation.constraints.NotBlank
import java.math.BigDecimal

data class DepositRequest(
    @field:NotBlank(message = "userUlid is required")
    val userUlid: String,

    @field:NotBlank(message = "accountUlid is required")
    val accountUlid: String,

    @field:NotBlank(message = "value is required")
    val value: BigDecimal
)

data class TransferRequest(
    @field:NotBlank(message = "fromUserUlid is required")
    val fromUserUlid: String,

    @field:NotBlank(message = "fromAccountUlid is required")
    val fromAccountUlid: String,

    @field:NotBlank(message = "toAccountUlid is required")
    val toAccountUlid: String,

    @field:NotBlank(message = "value is required")
    val value: BigDecimal

)