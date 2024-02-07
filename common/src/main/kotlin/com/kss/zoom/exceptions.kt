package com.kss.zoom

sealed class ZoomException(message: String) : RuntimeException(message) {
    class RequestFailedException(message: String) : ZoomException(message)
    class ResourceNotFoundException(message: String) : ZoomException("Resource not found: $message")
    class AuthorizationException(message: String) : ZoomException("Unauthorized: $message")
}