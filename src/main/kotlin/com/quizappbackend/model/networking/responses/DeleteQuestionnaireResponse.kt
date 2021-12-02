package com.quizappbackend.model.networking.responses

import kotlinx.serialization.Serializable

@Serializable
data class DeleteQuestionnaireResponse(
    val responseType: DeleteQuestionnaireResponseType
) {
    enum class  DeleteQuestionnaireResponseType {
        SUCCESSFUL,
        NOT_ACKNOWLEDGED
    }
}
