package com.example.domains.transaction.repository

import com.example.types.entity.Account
import org.springframework.data.jpa.repository.JpaRepository

interface TransactionAccount : JpaRepository<Account, String> {
    fun findByUlidAndIsDeletedFalse(accountUlid: String): Account?
}
