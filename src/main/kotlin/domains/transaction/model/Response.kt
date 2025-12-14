package com.example.domains.transaction.model

import jakarta.validation.constraints.NotBlank
import java.math.BigDecimal

data class DepositResponse(
    @field:NotBlank(message = "afterBalance is required")
    val afterBalance: BigDecimal
)

data class TransferResponse(
    @field:NotBlank(message = "afterFromBalance is required")
    val afterFromBalance: BigDecimal
)

