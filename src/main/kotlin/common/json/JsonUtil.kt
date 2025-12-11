package com.example.common.json

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

object JsonUtil {
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true // 불필요한 데이터는 받을 필요없음
    }

    fun <T> encodeToJson(v: T, serializer: KSerializer<T>): String {
        return json.encodeToString(serializer, v)
    }

    fun <T> decodeFromJson(v: String, serializer: KSerializer<T>): T {
        return json.decodeFromString(serializer, v)
    }
}