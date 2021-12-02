package com.quizappbackend.model.networking.requests

import com.quizappbackend.model.databases.mongodb.documents.faculty.MongoCourseOfStudies
import kotlinx.serialization.Serializable

@Serializable
data class InsertCourseOfStudiesRequest(
    val courseOfStudies: MongoCourseOfStudies
)
