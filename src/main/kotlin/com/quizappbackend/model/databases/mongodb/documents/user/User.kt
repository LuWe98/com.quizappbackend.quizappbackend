package com.quizappbackend.model.databases.mongodb.documents.user

import com.quizappbackend.model.databases.mongodb.documents.DocumentMarker
import io.ktor.util.date.*
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class User(
    @BsonId var id: String = ObjectId().toHexString(),
    var userName: String,
    var password: String,
    var role: Role,
    var lastModifiedTimestamp : Long = getTimeMillis()
) : DocumentMarker {

    val asAuthorInfo get() = AuthorInfo(id, userName)

}