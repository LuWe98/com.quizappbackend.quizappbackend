package com.quizappbackend.model.networking.responses

import com.quizappbackend.model.databases.mongodb.documents.MongoFaculty
import kotlinx.serialization.Serializable

@Serializable
data class SyncFacultiesResponse(
    val facultiesToInsert: List<MongoFaculty>,
    val facultiesToUpdate: List<MongoFaculty>,
    val facultyIdsToDelete: List<String>
)
