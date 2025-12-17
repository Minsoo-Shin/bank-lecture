package com.example.common.message

import com.example.common.logging.Logging
import org.slf4j.Logger
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.concurrent.CompletableFuture

@Component
class KafkaProducerAsync(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val log: Logger = Logging.getLogger(KafkaProducerAsync::class.java)
) {
    fun sendMessageAsync(topic: String, message: String): CompletableFuture<SendResult<String, String>> {
        return kafkaTemplate.send(topic, message).whenComplete { metadata, ex ->
            if (ex == null) {
                // hanlde success
                log.info("메시지 발행 성공 - topic: ${metadata.recordMetadata.topic()} - time: ${LocalDateTime.now()}")
            } else {
                // handle failure (TODO: 리트라이/DLT 추가)
                log.error(ex.stackTraceToString())
            }
        }
    }
}