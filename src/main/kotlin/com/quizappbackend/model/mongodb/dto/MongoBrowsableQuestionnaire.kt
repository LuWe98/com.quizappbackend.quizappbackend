package com.quizappbackend.model.mongodb.dto

import com.quizappbackend.model.mongodb.properties.AuthorInfo
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId

/**
 * This Document is a lightweight version of the regular MongoQuestionnaire without MongoQuestions
 */
@Serializable
data class MongoBrowsableQuestionnaire(
    @BsonId val id: String,
    val title: String,
    val authorInfo: AuthorInfo,
    val facultyIds: List<String> = emptyList(),
    val courseOfStudiesIds: List<String> = emptyList(),
    val subject: String = "",
    val questionCount: Int = 0,
    val lastModifiedTimestamp: Long
)