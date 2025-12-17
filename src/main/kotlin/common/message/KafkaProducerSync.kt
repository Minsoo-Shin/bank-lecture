package com.example.common.message

import com.example.common.exception.CustomException
import com.example.common.exception.ErrorCode
import com.example.common.logging.Logging
import io.netty.handler.timeout.TimeoutException
import org.slf4j.Logger
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class KafkaProducerSync(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val log: Logger = Logging.getLogger(KafkaProducerSync::class.java)
) {
    fun sendMessageSync(topic: String, message: String) {
        try {
            kafkaTemplate.send(topic, message).get(1, TimeUnit.SECONDS)
        } catch (e: TimeoutException) {
            throw CustomException(ErrorCode.SEND_MESSAGE_TIME_OUT, topic)
        } catch (e: Exception) {
            throw CustomException(ErrorCode.FAILED_TO_SEND_MESSAGE, topic)
        }
    }

}