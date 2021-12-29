package com.quizappbackend.model.mongodb.documents

import com.quizappbackend.model.mongodb.properties.Degree
import io.ktor.util.date.*
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId


//TODO -> Es ist bei dem Löschen eines Studiengangs möglich, dass die Fakultät noch im Fragebogen bleibt.
// Somit kann der Fragebogen noch mit der Fakultät gefunden werden, ohne einem Studiengang anzugehören
// Oder es kann passieren, dass der Fragebogen
@Serializable
data class MongoCourseOfStudies(
    @BsonId val id: String = ObjectId().toHexString(),
    val facultyIds: List<String> = emptyList(),
    val abbreviation: String,
    val name: String,
    val degree: Degree,
    val lastModifiedTimestamp : Long = getTimeMillis()
) : DocumentMarker