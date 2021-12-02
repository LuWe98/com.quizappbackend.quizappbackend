package com.quizappbackend.model.networking.responses

import kotlinx.serialization.Serializable

@Serializable
data class InsertCourseOfStudiesResponse(
    val responseType: InsertCourseOfStudiesResponseType
) {
    enum class  InsertCourseOfStudiesResponseType {
        SUCCESSFUL,
        ABBREVIATION_ALREADY_USED,
        NOT_ACKNOWLEDGED
    }
}