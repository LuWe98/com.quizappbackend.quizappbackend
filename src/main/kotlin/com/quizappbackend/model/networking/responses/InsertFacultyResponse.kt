package com.quizappbackend.model.networking.responses

import kotlinx.serialization.Serializable

@Serializable
data class InsertFacultyResponse(
    val responseType: InsertFacultyResponseType
) {
    enum class  InsertFacultyResponseType {
        SUCCESSFUL,
        ABBREVIATION_ALREADY_USED,
        NAME_ALREADY_USED,
        NOT_ACKNOWLEDGED
    }
}
