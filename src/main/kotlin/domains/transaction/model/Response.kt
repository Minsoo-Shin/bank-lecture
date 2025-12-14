package com.example.domains.transaction.model

import java.math.BigDecimal

data class DepositResponse(
    val afterBalance: BigDecimal
)

data class TransferResponse(
    val afterFromBalance: BigDecimal
)

