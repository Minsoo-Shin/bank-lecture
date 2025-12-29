package com.example.domains.transaction.controller

import com.example.domains.transaction.model.DepositRequest
import com.example.domains.transaction.model.DepositResponse
import com.example.domains.transaction.service.TransactionService
import com.example.types.dto.Response
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/transaction")
class TransactionController(
    private val transactionService: TransactionService,
) {

    @PostMapping("/deposit")
    fun deposit(
        @RequestBody(required = true) request: DepositRequest
    ): Response<DepositResponse> {
        return transactionService.deposit(request)
    }
}
