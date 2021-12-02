package com.quizappbackend.model.networking.responses

import com.quizappbackend.model.databases.mongodb.documents.questionnaire.MongoQuestionnaire
import kotlinx.serialization.Serializable

@Serializable
data class GetQuestionnaireResponse(
    val responseType: GetQuestionnaireResponseType,
    val mongoQuestionnaire: MongoQuestionnaire? = null
) {
    enum class GetQuestionnaireResponseType {
        SUCCESSFUL,
        QUESTIONNAIRE_NOT_FOUND,
    }
}