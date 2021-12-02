package com.quizappbackend.model.networking.requests

import kotlinx.serialization.Serializable

@Serializable
data class GetQuestionnaireRequest(
    val questionnaireId: String
)
