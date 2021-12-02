package com.quizappbackend.model.networking.requests

import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserNameRequest(
    val newUserName: String
)
