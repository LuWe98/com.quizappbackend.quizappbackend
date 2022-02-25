package com.quizappbackend.model.mongodb.documents

import com.quizappbackend.model.mongodb.properties.AuthorInfo
import com.quizappbackend.model.mongodb.properties.QuestionnaireVisibility
import io.ktor.util.date.*
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class MongoQuestionnaire(
    @BsonId val id: String = ObjectId().toHexString(),
    val title: String,
    val authorInfo: AuthorInfo,
    val facultyIds: List<String> = emptyList(),
    val courseOfStudiesIds: List<String> = emptyList(),
    val subject: String,
    val questionCount: Int,
    val questions: List<MongoQuestion> = emptyList(),
    val visibility: QuestionnaireVisibility = QuestionnaireVisibility.PRIVATE,
    val lastModifiedTimestamp: Long = getTimeMillis()
) : DocumentMarker {
    companion object {
        const val COLLECTION_NAME = "Questionnaires"
    }
}