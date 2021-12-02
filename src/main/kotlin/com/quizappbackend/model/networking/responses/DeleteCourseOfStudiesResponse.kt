package com.quizappbackend.model.networking.responses

import kotlinx.serialization.Serializable

@Serializable
data class DeleteCourseOfStudiesResponse(
    val responseType: DeleteCourseOfStudiesResponseType
) {
    enum class  DeleteCourseOfStudiesResponseType {
        SUCCESSFUL,
        NOT_ACKNOWLEDGED
    }
}
