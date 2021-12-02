package com.quizappbackend.model.networking.responses

import kotlinx.serialization.Serializable

@Serializable
data class BasicResponse(
    val isSuccessful: Boolean
)