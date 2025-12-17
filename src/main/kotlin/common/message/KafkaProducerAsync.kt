package com.example.common.message

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class KafkaProducerAsync(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val log: Logger = LoggerFactory.getLogger(KafkaProducerAsync::class.java)
) {
    fun sendMessageAsync(topic: String, message: String) {
        kafkaTemplate.send(topic, message).whenComplete { metadata, ex ->
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