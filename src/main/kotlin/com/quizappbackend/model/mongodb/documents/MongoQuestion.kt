package com.quizappbackend.model.mongodb.documents

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class MongoQuestion(
    @BsonId val id: String = ObjectId().toHexString(),
    val questionText: String,
    val isMultipleChoice: Boolean,
    val questionPosition: Int,
    val answers: List<MongoAnswer> = emptyList()
)