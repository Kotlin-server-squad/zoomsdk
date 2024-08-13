package com.kss.zoomsdk

interface Auth {
    suspend fun accessToken(): String
}
