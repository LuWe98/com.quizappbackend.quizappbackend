package com.quizappbackend.model.mongodb.properties

import kotlinx.serialization.Serializable

@Serializable
data class AuthorInfo(
    val userId: String,
    val userName: String
)