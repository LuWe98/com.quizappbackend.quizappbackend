package com.quizappbackend.model.mongodb.dto

import kotlinx.serialization.Serializable

@Serializable
data class LocallyDeletedQuestionnaire(
    val questionnaireId: String,
    val deleteWholeQuestionnaire : Boolean
)