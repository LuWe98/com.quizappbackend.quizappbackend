package com.quizappbackend.model.databases.mongodb.documents.faculty

import com.quizappbackend.model.databases.Degree
import com.quizappbackend.model.databases.mongodb.documents.DocumentMarker
import io.ktor.util.date.*
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class MongoCourseOfStudies(
    @BsonId var id: String = ObjectId().toHexString(),
    var facultyIds: List<String> = emptyList(),
    var abbreviation: String,
    var name: String,
    var degree: Degree,
    var lastModifiedTimestamp : Long = getTimeMillis()
) : DocumentMarker