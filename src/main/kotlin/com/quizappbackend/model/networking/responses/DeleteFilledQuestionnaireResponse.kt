package com.quizappbackend.model.networking.responses

import kotlinx.serialization.Serializable

@Serializable
data class DeleteFilledQuestionnaireResponse(
    val responseType: DeleteFilledQuestionnaireResponseType
) {
    enum class  DeleteFilledQuestionnaireResponseType {
        SUCCESSFUL,
        NOT_ACKNOWLEDGED
    }
}
