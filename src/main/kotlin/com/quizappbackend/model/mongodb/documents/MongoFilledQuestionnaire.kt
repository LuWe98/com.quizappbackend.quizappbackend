package com.quizappbackend.model.mongodb.documents

import kotlinx.serialization.Serializable
import org.bson.types.ObjectId

@Serializable
data class MongoFilledQuestionnaire(
    val questionnaireId: String = ObjectId().toHexString(),
    val userId: String = ObjectId().toHexString(),
    val questions: List<MongoFilledQuestion> = emptyList()
) : DocumentMarker {
    companion object {
        const val COLLECTION_NAME = "FilledQuestionnaires"
    }
}