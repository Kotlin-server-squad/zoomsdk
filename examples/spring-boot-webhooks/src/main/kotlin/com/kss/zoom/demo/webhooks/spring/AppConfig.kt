package com.kss.zoom.demo.webhooks.spring

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.kss.zoom.Zoom
import com.kss.zoom.auth.AccessToken
import com.kss.zoom.auth.RefreshToken
import com.kss.zoom.auth.UserTokens
import com.kss.zoom.sdk.Meetings
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
    fun meetings(): Meetings {
        return Zoom.create(
            clientId = clientId,
            clientSecret = clientSecret,
            verificationToken = verificationToken
        ).meetings(
            // TODO make optional: Server-to-Server Auth doesn't need these
            UserTokens(
                accessToken = AccessToken("accessToken", 3599),
                refreshToken = RefreshToken("refreshToken")
            )
        )
    }

    @Bean
    fun objectMapper(): ObjectMapper {
        return ObjectMapper().registerKotlinModule().configure(
            DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false
        ).enable(SerializationFeature.INDENT_OUTPUT)
    }
}