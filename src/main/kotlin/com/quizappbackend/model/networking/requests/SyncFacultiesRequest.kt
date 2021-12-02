package com.quizappbackend.model.networking.requests

import com.quizappbackend.model.databases.dto.FacultyIdWithTimeStamp
import kotlinx.serialization.Serializable

@Serializable
data class SyncFacultiesRequest(
    val localFacultyIdsWithTimeStamp: List<FacultyIdWithTimeStamp>,
)
