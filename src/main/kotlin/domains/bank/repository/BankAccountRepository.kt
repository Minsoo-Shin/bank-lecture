package com.example.domains.bank.repository

import com.example.types.entity.Account
import org.springframework.data.jpa.repository.JpaRepository

interface BankAccountRepository : JpaRepository<Account, String> {
    fun findByUlid(ulid: String): Account?
}