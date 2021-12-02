package com.quizappbackend.model.databases.dto

import kotlinx.serialization.Serializable

@Serializable
data class QuestionnaireIdWithTimestamp(
    val id: String,
    val lastModifiedTimestamp: Long
)