package com.quizappbackend.model.databases.mongodb.documents.questionnaire

import com.quizappbackend.model.databases.QuestionnaireVisibility
import com.quizappbackend.model.databases.mongodb.documents.DocumentMarker
import com.quizappbackend.model.databases.dto.MongoBrowsableQuestionnaire
import com.quizappbackend.model.databases.mongodb.documents.user.AuthorInfo
import io.ktor.util.date.*
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class MongoQuestionnaire(
    @BsonId var id: String = ObjectId().toHexString(),
    var title: String,
    var authorInfo: AuthorInfo,
    var facultyIds: List<String> = emptyList(),
    var courseOfStudiesIds: List<String> = emptyList(),
    var subject: String,
    var questions: List<MongoQuestion> = emptyList(),
    var questionnaireVisibility: QuestionnaireVisibility = QuestionnaireVisibility.PRIVATE,
    var lastModifiedTimestamp: Long = getTimeMillis()
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