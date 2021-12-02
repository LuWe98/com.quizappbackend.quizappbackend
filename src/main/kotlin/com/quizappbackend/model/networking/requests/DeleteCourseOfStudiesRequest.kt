package com.quizappbackend.model.networking.requests

import kotlinx.serialization.Serializable

@Serializable
data class DeleteCourseOfStudiesRequest(
    val courseOfStudiesId: String
)
