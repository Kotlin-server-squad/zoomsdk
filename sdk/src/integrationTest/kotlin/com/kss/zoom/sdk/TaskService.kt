package com.kss.zoom.sdk

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel

class TaskService(
    private val maxParallelTasks: Int,
    private val delayBetweenBatches: Long
) {
    private val tasksChannel = Channel<Task>(Channel.UNLIMITED)

    fun addTask(task: Task) {
        CoroutineScope(Dispatchers.Default).launch {
            tasksChannel.send(task)
        }
    }

    fun start() = CoroutineScope(Dispatchers.Default).launch {
        println("Starting TaskService")
        val currentBatch = mutableListOf<Deferred<TaskResponse>>()

        for (task in tasksChannel) {
            println("Processing task ${task.id}")
            if (currentBatch.size < maxParallelTasks) {
                currentBatch.add(executeTask(task))
            }

            if (currentBatch.size == maxParallelTasks) {
                currentBatch.awaitAll()
                currentBatch.clear()
                delay(delayBetweenBatches)
            }
        }

        if (currentBatch.isNotEmpty()) {
            currentBatch.awaitAll()
            delay(delayBetweenBatches)
        }

        tasksChannel.close()
    }

    private suspend fun executeTask(task: Task): Deferred<TaskResponse> = coroutineScope {
        async {
            delay(100)
            TaskResponse(task.id, TaskResponse.TaskStatus.SUCCESS)
        }
    }
}

data class Task (
    val id: String
)

data class TaskResponse(
    val id: String,
    val status: TaskStatus
){
    enum class TaskStatus {
        SUCCESS,
        FAILURE
    }
}