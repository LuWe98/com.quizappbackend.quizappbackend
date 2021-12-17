package com.quizappbackend.model.networking.requests

import com.quizappbackend.model.databases.mongodb.documents.MongoQuestionnaire
import kotlinx.serialization.Serializable

@Serializable
data class InsertQuestionnairesRequest(
    val mongoQuestionnaires: List<MongoQuestionnaire>
)
