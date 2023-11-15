package com.kss.zoomsdk.client

import com.kss.zoomsdk.client.plugins.*
import com.kss.zoomsdk.client.plugins.httpclient.configureLogging
import com.kss.zoomsdk.client.plugins.httpclient.configureSerialization
import io.ktor.client.*
import io.ktor.client.engine.cio.*

interface IZoomClient {
    fun auth(): IAuthorization
    fun meetings(): IMeetings
}

class ZoomClient(config: ZoomClientConfig, client: HttpClient) : IZoomClient {

    private val http = Http(config, client)
    private val authorization = Authorization(config, http)
    private val meetings = Meetings(config.baseUrl, http)

    companion object {
        fun create(clientId: String, clientSecret: String, baseUrl: String = "https://zoom.us"): IZoomClient {
            return ZoomClient(
                ZoomClientConfig.create(clientId, clientSecret, baseUrl),
                HttpClient(CIO) {
                    configureLogging()
                    configureSerialization()
                }
            )
        }
    }

    override fun auth(): IAuthorization {
        return authorization
    }

    override fun meetings(): IMeetings {
        return meetings
    }
}

data class ZoomClientConfig(
    val clientId: ClientId,
    val clientSecret: ClientSecret,
    val baseUrl: String
) {
    companion object {
        fun create(clientId: String, clientSecret: String, baseUrl: String = "https://zoom.us"): ZoomClientConfig {
            return ZoomClientConfig(ClientId(clientId), ClientSecret(clientSecret), baseUrl)
        }
    }
}