package com.quizappbackend.model.networking.responses

import com.quizappbackend.model.databases.mongodb.documents.questionnairefilled.MongoFilledQuestionnaire
import com.quizappbackend.model.databases.mongodb.documents.questionnaire.MongoQuestionnaire
import kotlinx.serialization.Serializable

@Serializable
data class SyncQuestionnairesResponse(
    val mongoQuestionnaires: List<MongoQuestionnaire>,
    val mongoFilledQuestionnaires: List<MongoFilledQuestionnaire>,
    val questionnaireIdsToUnsync: List<String>
)