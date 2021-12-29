package com.quizappbackend.model.mongodb.documents

import kotlinx.serialization.Serializable

@Serializable
data class MongoFilledQuestion(
    val questionId : String,
    val selectedAnswerIds : List<String> = emptyList()
)