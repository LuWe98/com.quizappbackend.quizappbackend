package com.quizappbackend.model.networking.requests

import kotlinx.serialization.Serializable

@Serializable
data class DeleteUsersRequest(
    val userIds: List<String>
)
