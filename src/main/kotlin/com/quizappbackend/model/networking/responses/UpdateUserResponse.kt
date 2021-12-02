package com.quizappbackend.model.networking.responses

import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserResponse(
    val responseType: UpdateUserResponseType,
) {
    enum class UpdateUserResponseType {
        UPDATE_SUCCESSFUL,
        NOT_ACKNOWLEDGED,
        USERNAME_ALREADY_TAKEN,
        LAST_CHANGE_TO_CLOSE,
        USER_DOES_NOT_EXIST
    }
}
