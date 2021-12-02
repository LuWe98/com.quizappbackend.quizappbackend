package com.quizappbackend.model.networking.responses

import kotlinx.serialization.Serializable

@Serializable
data class DeleteFacultyResponse(
    val responseType: DeleteFacultyResponseType
) {
    enum class  DeleteFacultyResponseType {
        SUCCESSFUL,
        NOT_ACKNOWLEDGED
    }
}
