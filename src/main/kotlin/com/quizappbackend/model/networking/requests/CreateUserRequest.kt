package com.quizappbackend.model.networking.requests

import com.quizappbackend.model.databases.mongodb.documents.user.Role
import kotlinx.serialization.Serializable

@Serializable
data class CreateUserRequest(
    val userName: String,
    val password: String,
    val role: Role
)
