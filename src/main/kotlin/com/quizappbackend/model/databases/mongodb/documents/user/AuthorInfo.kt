package com.quizappbackend.model.databases.mongodb.documents.user

import kotlinx.serialization.Serializable

@Serializable
data class AuthorInfo(
    val userId: String,
    val userName: String
)