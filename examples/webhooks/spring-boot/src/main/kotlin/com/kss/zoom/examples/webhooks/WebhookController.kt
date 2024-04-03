package com.kss.zoom.examples.webhooks

import com.fasterxml.jackson.databind.ObjectMapper
import com.kss.zoom.sdk.common.callAsync
import com.kss.zoom.sdk.meetings.IMeetings
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/webhooks")
class WebhookController(
    private val meetings: IMeetings,
    private val objectMapper: ObjectMapper
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @RequestMapping("/meetings")
    fun meetingsWebhook(
        request: HttpServletRequest,
        @RequestHeader("x-zm-request-timestamp") timestamp: Long,
        @RequestHeader("x-zm-signature") signature: String,
    ) {
        val payload = request.reader.readText()
        callAsync {
            meetings.onMeetingCreated(payload, timestamp, signature) {
                notifyClients(it)
            }
        }
        logger.info("Responding OK to meetings webhook")
    }

    private fun <T> notifyClients(event: T) {
        val message = objectMapper.writeValueAsString(event)
        logger.info(message)
    }
}