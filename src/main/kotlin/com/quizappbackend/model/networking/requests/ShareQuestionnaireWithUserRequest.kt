package com.quizappbackend.model.networking.requests

import kotlinx.serialization.Serializable

@Serializable
data class ShareQuestionnaireWithUserRequest(
    val questionnaireId: String,
    val userName: String,
    val canEdit: Boolean
)