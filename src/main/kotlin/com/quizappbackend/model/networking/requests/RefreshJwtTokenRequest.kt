package com.quizappbackend.model.networking.requests

import kotlinx.serialization.Serializable

@Serializable
data class RefreshJwtTokenRequest(
    val userName: String,
    val password: String
)