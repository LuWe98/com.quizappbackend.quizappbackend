package com.quizappbackend.model.networking.responses

import kotlinx.serialization.Serializable

@Serializable
data class RegisterUserResponse(
    val responseType: RegisterUserResponseType,
) {
    enum class RegisterUserResponseType {
        REGISTER_SUCCESSFUL,
        NOT_ACKNOWLEDGED,
        USER_ALREADY_EXISTS
    }
}