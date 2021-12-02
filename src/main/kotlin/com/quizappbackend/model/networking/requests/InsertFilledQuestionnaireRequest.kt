package com.quizappbackend.model.networking.requests

import com.quizappbackend.model.databases.mongodb.documents.questionnairefilled.MongoFilledQuestionnaire
import kotlinx.serialization.Serializable

@Serializable
data class InsertFilledQuestionnaireRequest(
    val shouldBeIgnoredWhenAnotherIsPresent : Boolean,
    val mongoFilledQuestionnaire: MongoFilledQuestionnaire
)
