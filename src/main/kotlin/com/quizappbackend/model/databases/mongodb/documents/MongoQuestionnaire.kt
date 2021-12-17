package com.quizappbackend.model.databases.mongodb.documents

import com.quizappbackend.model.databases.QuestionnaireVisibility
import com.quizappbackend.model.databases.dto.MongoBrowsableQuestionnaire
import com.quizappbackend.model.databases.mongodb.documents.user.AuthorInfo
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
    val questions: List<MongoQuestion> = emptyList(),
    val questionnaireVisibility: QuestionnaireVisibility = QuestionnaireVisibility.PRIVATE,
    val lastModifiedTimestamp: Long = getTimeMillis()
) : DocumentMarker {

    fun asMongoQuestionnaireBrowse() =
        MongoBrowsableQuestionnaire(
            id = id,
            title = title,
            authorInfo = authorInfo,
            facultyIds = facultyIds,
            courseOfStudiesIds = courseOfStudiesIds,
            subject = subject,
            questionCount = questions.size,
            lastModifiedTimestamp = lastModifiedTimestamp
        )

}