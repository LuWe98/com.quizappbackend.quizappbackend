package com.quizappbackend.model.mongodb.properties

import kotlinx.serialization.Serializable

@Serializable
data class AuthorId(val userId: String)