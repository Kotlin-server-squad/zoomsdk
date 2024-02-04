package com.kss.zoom.meetings

import com.kss.zoom.auth.AccessToken
import com.kss.zoom.auth.IAuthorization

interface IMeetings {
    fun auth(): IAuthorization

    suspend fun listScheduled(accessToken: AccessToken): List<Meeting>
}

class Meetings private constructor(private val authorization: IAuthorization) : IMeetings {
    override fun auth(): IAuthorization = authorization

    companion object {
        fun create(authorization: IAuthorization): IMeetings = Meetings(authorization)
    }

    override suspend fun listScheduled(accessToken: AccessToken): List<Meeting> {
        TODO()
    }
}