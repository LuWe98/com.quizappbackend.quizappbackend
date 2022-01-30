package com.quizappbackend.model.mongodb.dto

import com.quizappbackend.model.ktor.paging.BrowsableQuestionnairePageKeys
import com.quizappbackend.model.mongodb.documents.MongoQuestionnaire
import kotlin.reflect.KProperty1

enum class BrowsableQuestionnaireOrderBy(val orderByProperty: KProperty1<MongoQuestionnaire, Any?>) {
    TITLE(MongoQuestionnaire::title),
    LAST_UPDATED(MongoQuestionnaire::lastModifiedTimestamp);

    fun getValueOfQuestionnairePage(pageKeys: BrowsableQuestionnairePageKeys) = when(this) {
        TITLE -> pageKeys.title
        LAST_UPDATED -> pageKeys.timeStamp
    }
}