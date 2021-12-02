package com.quizappbackend.model.networking.responses

import kotlinx.serialization.Serializable

@Serializable
data class ChangeQuestionnaireVisibilityResponse(
    val responseType: ChangeQuestionnaireVisibilityResponseType
) {
    enum class ChangeQuestionnaireVisibilityResponseType{
        SUCCESSFUL,
        NOT_ACKNOWLEDGED
    }
}