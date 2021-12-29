package com.quizappbackend.model.mongodb.dto

import kotlinx.serialization.Serializable

@Serializable
data class FacultyIdWithTimeStamp(
    val facultyId: String,
    val lastModifiedTimestamp: Long
)