package com.quizappbackend.model.networking.responses

import kotlinx.serialization.Serializable

@Serializable
data class InsertQuestionnairesResponse(
    val responseType: InsertQuestionnairesResponseType
) {
    enum class  InsertQuestionnairesResponseType {
        SUCCESSFUL,
        NOT_ACKNOWLEDGED
    }
}
