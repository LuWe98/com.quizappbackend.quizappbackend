package com.quizappbackend.model.networking.requests

import com.quizappbackend.model.databases.QuestionnaireVisibility
import kotlinx.serialization.Serializable

@Serializable
data class ChangeQuestionnaireVisibilityRequest(
    val questionnaireId: String,
    val newVisibility: QuestionnaireVisibility
)
