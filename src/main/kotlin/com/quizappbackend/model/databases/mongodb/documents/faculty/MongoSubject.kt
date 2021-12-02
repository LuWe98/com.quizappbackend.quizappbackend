package com.quizappbackend.model.databases.mongodb.documents.faculty

import com.quizappbackend.model.databases.mongodb.documents.DocumentMarker
import io.ktor.util.date.*
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

//TODO -> Schauen ob es das überhaupt braucht oder nicht
@Serializable
data class MongoSubject(
    @BsonId var id: String = ObjectId().toHexString(),
    var abbreviation: String,
    var name: String,
    var lastModifiedTimestamp : Long = getTimeMillis()
) : DocumentMarker