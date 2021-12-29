package com.quizappbackend.model.mongodb.documents

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class MongoAnswer(
    @BsonId val id: String = ObjectId().toHexString(),
    val answerText: String,
    val answerPosition: Int,
    val isAnswerCorrect: Boolean
)