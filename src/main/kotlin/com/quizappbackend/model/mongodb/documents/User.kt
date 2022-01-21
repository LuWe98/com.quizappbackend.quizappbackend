package com.quizappbackend.model.mongodb.documents

import com.quizappbackend.model.mongodb.properties.AuthorInfo
import com.quizappbackend.model.mongodb.properties.Role
import io.ktor.util.date.*
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class User(
    @BsonId val id: String = ObjectId().toHexString(),
    val userName: String,
    val password: String = "",
    val role: Role = Role.USER,
    val lastModifiedTimestamp : Long = getTimeMillis()
) : DocumentMarker {

    fun asAuthorInfo() = AuthorInfo(id, userName)

}