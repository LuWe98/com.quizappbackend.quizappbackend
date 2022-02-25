package com.quizappbackend.model.mongodb.documents

import com.quizappbackend.model.mongodb.properties.Degree
import io.ktor.util.date.*
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class MongoCourseOfStudies(
    @BsonId val id: String = ObjectId().toHexString(),
    val facultyIds: List<String> = emptyList(),
    val abbreviation: String,
    val name: String,
    val degree: Degree,
    val lastModifiedTimestamp : Long = getTimeMillis()
) : DocumentMarker {
    companion object {
        const val COLLECTION_NAME = "CourseOfStudies"
    }
}