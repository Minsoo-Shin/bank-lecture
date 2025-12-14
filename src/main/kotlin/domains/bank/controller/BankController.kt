package com.example.domains.bank.controller

import com.example.domains.bank.service.BankService
import com.example.types.dto.Response
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal

@RestController
@RequestMapping("/api/v1/bank")
class BankController(
    private val bankService: BankService,
) {

    @PostMapping("/create/{userUlid}")
    fun createAccount(
        @PathVariable(required = true) userUlid: String
    ): Response<String> {
        return bankService.createAccount(userUlid)
    }

    @GetMapping("/balance/{userUlid}/{accountUlid}")
    fun balance(
        @PathVariable(required = true) userUlid: String,
        @PathVariable(required = true) accountUlid: String,
    ): Response<BigDecimal> {
        return bankService.balance(userUlid, accountUlid)
    }

    @DeleteMapping("/remove/{userUlid}/{accountUlid}")
    fun removeAccount(
        @PathVariable(required = true) userUlid: String,
        @PathVariable(required = true) accountUlid: String,
    ): Response<String> {
        return bankService.removeAccount(userUlid, accountUlid)
    }

}