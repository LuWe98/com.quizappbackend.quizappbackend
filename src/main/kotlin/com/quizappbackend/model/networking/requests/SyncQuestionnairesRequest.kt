package com.quizappbackend.model.networking.requests

import com.quizappbackend.model.databases.dto.QuestionnaireIdWithTimestamp
import kotlinx.serialization.Serializable

@Serializable
data class SyncQuestionnairesRequest(
    val syncedQuestionnaireIdsWithTimestamp : List<QuestionnaireIdWithTimestamp>,
    val unsyncedQuestionnaireIds: List<String>,
    val locallyDeletedQuestionnaireIds: List<String>
)
