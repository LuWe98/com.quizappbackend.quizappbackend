package com.quizappbackend.model.ktor.paging

import kotlinx.serialization.Serializable

@Serializable
data class BrowsableQuestionnairePageKeys(
    val id: String = "",
    val title: String = "",
    val timeStamp: Long = 0
) {
    companion object {
        val EMPTY_QUESTIONNAIRE_PAGE_KEYS = BrowsableQuestionnairePageKeys()
    }
}