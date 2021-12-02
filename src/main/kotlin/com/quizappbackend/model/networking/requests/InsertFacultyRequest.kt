package com.quizappbackend.model.networking.requests

import com.quizappbackend.model.databases.mongodb.documents.faculty.MongoFaculty
import kotlinx.serialization.Serializable

@Serializable
data class InsertFacultyRequest(
    val faculty: MongoFaculty
)