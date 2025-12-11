package com.example.domains.auth.service

import com.example.common.exception.CustomException
import com.example.common.exception.ErrorCode
import com.example.config.OAuth2Config
import com.example.interfaces.OAuth2TokenResponse
import com.example.interfaces.OAuth2UserResponse
import com.example.interfaces.OAuthServiceInterface
import org.springframework.stereotype.Service

private const val key = "github"

@Service(key)
class GithubAuthService(
    private val config: OAuth2Config
) : OAuthServiceInterface {
    private val oAuthInfo = config.providers[key] ?: throw CustomException(ErrorCode.AUTH_CONFIG_NOT_FOUND, key)

    override val providerName: String = key

    override fun getToken(code: String): OAuth2TokenResponse {
        TODO("Not yet implemented")
    }

    override fun getUserInfo(accessToken: String): OAuth2UserResponse {
        TODO("Not yet implemented")
    }
}