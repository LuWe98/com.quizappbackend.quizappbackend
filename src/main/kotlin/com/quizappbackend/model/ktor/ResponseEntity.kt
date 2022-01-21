package com.quizappbackend.model.ktor

import io.ktor.http.*

data class ResponseEntity<T>(
    val statusCode: HttpStatusCode,
    val response: T
)