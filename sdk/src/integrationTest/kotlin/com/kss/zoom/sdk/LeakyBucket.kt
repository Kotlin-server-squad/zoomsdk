package com.kss.zoom.sdk

import com.kss.zoom.sdk.common.call
import com.kss.zoom.sdk.users.IUsers
import com.kss.zoom.sdk.users.model.domain.CreateUser
import com.kss.zoom.sdk.users.model.domain.User
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.sync.withPermit
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext

class LeakyBucket(
    private val bucketCapacity: Int,
    private val requestsPerSecond: Int,
    private val users: IUsers,
) {

    private val bucket = Channel<Deferred<User>>(bucketCapacity)
    private val semaphore = Semaphore(permits = requestsPerSecond)
    private val coroutineScope = CoroutineScope(Dispatchers.Default + CoroutineName("LeakyBucket"))
//    init {
//        coroutineScope.launch {
//            while(isActive){
//                delay(250L)
//                bucket.tryReceive()
//            }
//        }
//    }

    init {
        coroutineScope.launch {
            while (isActive){
                repeat(requestsPerSecond){
                    if(!bucket.isEmpty) {
                        bucket.receive()
                    }
                }
                delay(1000L)
            }
        }
    }

    suspend fun addRequest(block: suspend () -> User): Deferred<User> {
        return withContext(Dispatchers.Default) {
            semaphore.withPermit {
                val deferredResult = async { block() }
                bucket.send(deferredResult)
                deferredResult
            }
        }
    }
    suspend fun enqueueRequest(request: CreateUser): User {
       return semaphore.withPermit {
           delay(1000L)
            if (bucket.isClosedForSend) {
                throw IllegalStateException("Bucket is closed")
            }
            val bucketRequest = coroutineScope.async {
                call {
                    users.create(request)
                }
            }
            val resp = bucket.send(bucketRequest)
            bucketRequest.await()
        }
    }
}