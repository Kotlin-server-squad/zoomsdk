package com.kss.zoom.model

sealed interface CallResult<out T> {
    data class Success<T>(val data: T) : CallResult<T>

    sealed interface Error : CallResult<Nothing> {
        val message: String

        data object BadRequest : Error {
            override val message: String
                get() = "Bad request"
        }

        data object Forbidden : Error {
            override val message: String
                get() = "Forbidden"
        }

        data object NotFound : Error {
            override val message: String
                get() = "Not found"
        }

        data object TooManyRequests : Error {
            override val message: String
                get() = "Too many requests"
        }

        data object Unauthorized : Error {
            override val message: String
                get() = "Unauthorized"
        }

        data class Other(override val message: String) : Error
    }
}

