package com.example.common.cache

object RedisKeyProvider {
    private const val BANK_MUTEX_KEY = "bankMutex"
    private const val HISTORY_CACHE_KEY = "history"

    fun bankMutexKey(accountUlid: String): String {
        return "$BANK_MUTEX_KEY:$accountUlid"
    }

    fun historyCacheKey(ulid: String, accountUlid: String): String {
        return "$HISTORY_CACHE_KEY:$ulid:$accountUlid"
    }

}