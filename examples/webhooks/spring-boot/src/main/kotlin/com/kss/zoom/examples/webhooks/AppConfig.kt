package com.kss.zoom.examples.webhooks

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.kss.zoom.Zoom
import com.kss.zoom.sdk.meetings.IMeetings
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AppConfig {

    @Value("\${zoom.client-id}")
    private lateinit var clientId: String

    @Value("\${zoom.client-secret}")
    private lateinit var clientSecret: String

    @Value("\${zoom.verification-token}")
    private lateinit var verificationToken: String

    @Bean
    fun meetings(): IMeetings {
        return Zoom.create(
            clientId = clientId,
            clientSecret = clientSecret,
            verificationToken = verificationToken
        ).meetings()
    }

    @Bean
    fun objectMapper(): ObjectMapper {
        return ObjectMapper().registerKotlinModule().configure(
            DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false
        ).enable(SerializationFeature.INDENT_OUTPUT)
    }
}