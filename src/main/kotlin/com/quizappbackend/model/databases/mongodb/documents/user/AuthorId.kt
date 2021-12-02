package com.quizappbackend.model.databases.mongodb.documents.user

import kotlinx.serialization.Serializable

@Serializable
data class AuthorId(val userId: String)