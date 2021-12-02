package com.quizappbackend.model.networking.requests

import kotlinx.serialization.Serializable

@Serializable
data class DeleteFilledQuestionnaireRequest(
    val questionnaireIds: List<String>
)