package com.quizappbackend.model.networking.requests

import com.quizappbackend.model.databases.mongodb.documents.MongoFilledQuestionnaire
import kotlinx.serialization.Serializable

@Serializable
data class InsertFilledQuestionnairesRequest(
    val mongoFilledQuestionnaires: List<MongoFilledQuestionnaire>
)
