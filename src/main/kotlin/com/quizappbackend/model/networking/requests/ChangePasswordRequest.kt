package com.quizappbackend.model.networking.requests

import kotlinx.serialization.Serializable

@Serializable
data class ChangePasswordRequest(
    val newPassword: String
)