package com.example.domains.transaction.repository

import com.example.types.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface TransactionUser : JpaRepository<User, String> {
    fun findByUlid(ulid: String): User?
}
