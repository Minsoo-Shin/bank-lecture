package com.example.common.message

enum class KafkaTopic(
    val topic: String,
) {
    Transactions("transactions");

    override fun toString(): String {
        return topic
    }
}
