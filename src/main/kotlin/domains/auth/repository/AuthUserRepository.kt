package com.example.domains.auth.repository

import com.example.types.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface AuthUserRepository : JpaRepository<User, String> {
    fun existsByUsername(username: String): Boolean

    @Modifying
    @Query("UPDATE User u SET u.accessToken = :accessToken WHERE u.username = :username")
    fun updateAccessTokenByUsername(
        @Param("username") username: String,
        @Param("accessToken") token: String,
    )
}