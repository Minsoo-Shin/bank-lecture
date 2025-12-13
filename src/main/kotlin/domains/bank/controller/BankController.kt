package com.example.domains.bank.controller

import com.example.domains.bank.service.BankService
import com.example.types.dto.Response
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
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

    @PostMapping("/remove/{userUlid}/{accountUlid}")
    fun removeAccount(
        @PathVariable(required = true) userUlid: String,
        @PathVariable(required = true) accountUlid: String,
    ): Response<String> {
        return bankService.removeAccount(userUlid, accountUlid)
    }

}