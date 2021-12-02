package com.quizappbackend.model.databases.mongodb.documents.user

import kotlinx.serialization.Serializable

@Serializable
data class SharedWithInfo(
    val userId: String,
    val canEdit: Boolean
)