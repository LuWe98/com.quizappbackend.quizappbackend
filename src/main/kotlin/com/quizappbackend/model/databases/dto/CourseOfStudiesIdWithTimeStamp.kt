package com.quizappbackend.model.databases.dto

import kotlinx.serialization.Serializable

@Serializable
data class CourseOfStudiesIdWithTimeStamp(
    val courseOfStudiesId: String,
    val lastModifiedTimestamp: Long
)