package com.quizappbackend.model.networking.responses

import kotlinx.serialization.Serializable

@Serializable
data class ChangePasswordResponse(
    val newToken: String? = null,
    val responseType: ChangePasswordResponseType
) {
    enum class ChangePasswordResponseType{
        SUCCESSFUL,
        NOT_ACKNOWLEDGED
    }
}