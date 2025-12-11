package com.example.domains.auth.service

import com.example.common.exception.CustomException
import com.example.common.exception.ErrorCode
import com.example.common.jwt.JwtProvider
import com.example.common.logging.Logging
import com.example.common.transaction.Transactional
import com.example.domains.auth.repository.AuthUserRepository
import com.example.interfaces.OAuthServiceInterface
import com.example.types.entity.User
import com.github.f4b6a3.ulid.UlidCreator
import org.slf4j.Logger
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val oAuth2Service: Map<String, OAuthServiceInterface>,
    private val jwtProvider: JwtProvider,
    private val logger: Logger = Logging.getLogger(AuthService::class.java),
    private val transaction: Transactional,
    private val authUserRepository: AuthUserRepository,
) {
    fun handleAuth(state: String, code: String): String = Logging.logFor(logger) { log ->
        val provider = state.lowercase()
        log["provider"] = provider

        val callService = oAuth2Service[provider] ?: throw CustomException(ErrorCode.PROVIDER_NOT_FOUND, provider)

        val accessToken = callService.getToken(code)
        val userInfo = callService.getUserInfo(accessToken = accessToken.accessToken)

        val token = jwtProvider.createToken(
            platform = provider,
            email = userInfo.email,
            name = userInfo.name,
            id = userInfo.id
        )

        val username = (userInfo.name ?: userInfo.email).toString()

        transaction.run {
            val exist = authUserRepository.existsByUsername(username)

            if (exist) {
                authUserRepository.updateAccessTokenByUsername(username, token)
            } else {
                val ulid = UlidCreator.getUlid().toString()

                val user = User(ulid, username, token)

                authUserRepository.save(user)

            }

        }

        return@logFor token
    }

    fun verifyToken(authorization: String) {
        jwtProvider.verifyToken(authorization.removePrefix("Bearer "))
    }
}