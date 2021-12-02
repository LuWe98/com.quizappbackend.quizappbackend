package com.quizappbackend.model.networking.requests

import kotlinx.serialization.Serializable

@Serializable
data class LoginUserRequest(
    val userName: String,
    val password: String
)