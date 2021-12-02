package com.quizappbackend.model.networking.responses

import kotlinx.serialization.Serializable

@Serializable
data class CreateUserResponse(
    val responseType: CreateUserResponseType,
) {
    enum class CreateUserResponseType {
        CREATION_SUCCESSFUL,
        NOT_ACKNOWLEDGED,
        USER_ALREADY_EXISTS
    }
}