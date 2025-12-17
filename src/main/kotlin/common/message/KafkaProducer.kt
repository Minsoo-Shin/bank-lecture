package com.example.common.message

import com.example.common.exception.CustomException
import com.example.common.exception.ErrorCode
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.concurrent.CompletableFuture

@Component
class KafkaProducer(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val log: Logger = LoggerFactory.getLogger(KafkaProducer::class.java)
) {
    fun sendMessage(topic: String, message: String) {
        val future: CompletableFuture<SendResult<String, String>> = kafkaTemplate.send(topic, message)
        future.whenComplete { metadata, ex ->
            if (ex == null) {
                // hanlde success
                log.info("메시지 발행 성공 - topic: $topic - time: ${LocalDateTime.now()}")
            } else {
                // handle failure
                throw CustomException(ErrorCode.FAILED_TO_SEND_MESSAGE, topic)
            }
        }
    }
}