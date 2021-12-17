package com.quizappbackend.model.databases.mongodb.documents

import io.ktor.util.date.*
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

//TODO -> Schauen ob es das überhaupt braucht oder nicht
@Serializable
data class MongoSubject(
    @BsonId val id: String = ObjectId().toHexString(),
    val abbreviation: String,
    val name: String,
    val lastModifiedTimestamp : Long = getTimeMillis()
) : DocumentMarker