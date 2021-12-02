package com.quizappbackend.model.networking.requests

import kotlinx.serialization.Serializable

@Serializable
data class SyncUserDataRequest(
    val userId: String
)