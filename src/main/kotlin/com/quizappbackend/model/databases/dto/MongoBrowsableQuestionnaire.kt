package com.quizappbackend.model.databases.dto

import com.quizappbackend.model.databases.mongodb.documents.user.AuthorInfo
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId

/**
 * This Entity is a lightweight version of the regular MongoQuestionnaire in order to have less size
 */
@Serializable
data class MongoBrowsableQuestionnaire(
    @BsonId val id: String,
    val title: String,
    val authorInfo: AuthorInfo,
    val facultyIds: List<String>,
    val courseOfStudiesIds: List<String>,
    val subject: String,
    val questionCount: Int = 0,
    val lastModifiedTimestamp: Long
)