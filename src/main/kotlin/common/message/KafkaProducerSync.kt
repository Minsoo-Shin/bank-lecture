package com.example.common.message

import com.example.common.exception.CustomException
import com.example.common.exception.ErrorCode
import com.example.common.logging.Logging
import org.slf4j.Logger
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

private const val SEND_TIMEOUT_SECONDS = 3L

@Component
class KafkaProducerSync(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val log: Logger = Logging.getLogger(KafkaProducerSync::class.java)
) {
    fun sendMessageSync(topic: String, message: String) {
        try {
            kafkaTemplate.send(topic, message).get(SEND_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        } catch (e: TimeoutException) {
            log.error("메시지 전송 타임아웃 - topic: {}", topic, e)
            throw CustomException(ErrorCode.SEND_MESSAGE_TIME_OUT, topic)
        } catch (e: Exception) {
            log.error("메시지 전송 실패 - topic: {}", topic, e)
            throw CustomException(ErrorCode.FAILED_TO_SEND_MESSAGE, topic)
        }
    }

}