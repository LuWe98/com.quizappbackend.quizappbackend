package com.quizappbackend.model.mongodb.properties

import kotlinx.serialization.Serializable

@Serializable
data class SharedWithInfo(
    val userId: String,
    val canEdit: Boolean
)