package com.quizappbackend.model.mongodb.documents

import com.quizappbackend.model.mongodb.properties.QuestionnaireVisibility
import com.quizappbackend.model.mongodb.dto.MongoBrowsableQuestionnaire
import com.quizappbackend.model.mongodb.properties.AuthorInfo
import io.ktor.util.date.*
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

//TODO -> Idee ist Fakultäten hier rauszunehmen und nur noch die Liste von CoursesOfStudies da zu haben.
// Es soll eine gecachete Liste aller Fakultäten mit deren Studiengängen geben
// Wenn es änderungen in dieser Liste gibt, wird das auch in dem Cache abgebildet
// Bei online Suchanfragen nach Fragebögen mit ihrer Fakultät wird dann in der Liste nachgeschaut
// -> Somit braucht es hier keine FacultyId Liste
// -> Die gecachte Liste ist zudem relativ klein und mappt nur die IDS

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

    fun asMongoQuestionnaireBrowse() = MongoBrowsableQuestionnaire(
        id = id,
        title = title,
        authorInfo = authorInfo,
        facultyIds = facultyIds,
        courseOfStudiesIds = courseOfStudiesIds,
        subject = subject,
        questionCount = questionCount,
        lastModifiedTimestamp = lastModifiedTimestamp
    )

}