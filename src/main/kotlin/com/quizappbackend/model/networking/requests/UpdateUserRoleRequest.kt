package com.quizappbackend.model.networking.requests

import com.quizappbackend.model.databases.mongodb.documents.user.Role
import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserRoleRequest(
    val userId: String,
    val newRole: Role
)
