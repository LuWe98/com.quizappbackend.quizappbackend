package com.quizappbackend.model.networking.requests

import kotlinx.serialization.Serializable

@Serializable
data class DeleteQuestionnaireRequest(
    val questionnaireIds: List<String>
)
