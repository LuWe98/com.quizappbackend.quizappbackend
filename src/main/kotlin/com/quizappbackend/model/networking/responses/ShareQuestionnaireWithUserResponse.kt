package com.quizappbackend.model.networking.responses

import kotlinx.serialization.Serializable

@Serializable
data class ShareQuestionnaireWithUserResponse(
    val responseType: ShareQuestionnaireWithUserResponseType
) {
    enum class ShareQuestionnaireWithUserResponseType {
        SUCCESSFUL,
        ALREADY_SHARED_WITH_USER,
        USER_DOES_NOT_EXIST,
        QUESTIONNAIRE_DOES_NOT_EXIST,
        NOT_ACKNOWLEDGED,
        USER_IS_OWNER_OF_QUESTIONNAIRE
    }
}