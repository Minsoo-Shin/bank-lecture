package com.example.common.message

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class KafkaProducer(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val log: Logger = LoggerFactory.getLogger(KafkaProducer::class.java)
) {
    fun sendMessage(topic: String, message: String) {
        kafkaTemplate.send(topic, message).whenComplete { metadata, ex ->
            if (ex == null) {
                // hanlde success
                log.info("메시지 발행 성공 - topic: ${metadata.recordMetadata.topic()} - time: ${LocalDateTime.now()}")
            } else {
                // handle failure
                log.error(ex.stackTraceToString())
            }
        }
    }
}