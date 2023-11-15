package com.kss.zoomsdk.client.plugins

import java.time.Instant

interface IMeetings {
    suspend fun listScheduled(accessToken: String): List<Meeting>
}

class Meetings(private val baseUrl: String, private val http: Http) : IMeetings {
    override suspend fun listScheduled(accessToken: String): List<Meeting> {
        TODO("Not yet implemented")
    }
}

data class Meeting(val id: String, val startTime: Instant, val duration: Int)