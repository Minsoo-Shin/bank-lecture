package com.example.domains.auth.service

import com.example.common.exception.CustomException
import com.example.common.exception.ErrorCode
import com.example.common.jwt.JwtProvider
import com.example.interfaces.OAuthServiceInterface
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val oAuth2Service: Map<String, OAuthServiceInterface>,
    private val jwtProvider: JwtProvider,
) {
    fun handleAuth(state: String, code: String): String {
        val provider = state.lowercase()

        val callService = oAuth2Service[provider] ?: throw CustomException(ErrorCode.PROVIDER_NOT_FOUND, provider)

        val accessToken = callService.getToken(code)
        val userInfo = callService.getUserInfo(accessToken = accessToken.accessToken)
        return jwtProvider.createToken(
            platform = provider,
            email = userInfo.email,
            name = userInfo.name,
            id = userInfo.id
        )
    }
}