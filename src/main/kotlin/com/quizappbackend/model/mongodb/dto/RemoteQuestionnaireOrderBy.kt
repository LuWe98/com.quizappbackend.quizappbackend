package com.quizappbackend.model.mongodb.dto

import com.quizappbackend.model.mongodb.documents.MongoQuestionnaire
import com.quizappbackend.model.mongodb.properties.AuthorInfo
import org.litote.kmongo.div
import kotlin.reflect.KProperty1

enum class RemoteQuestionnaireOrderBy(val orderByProperty: KProperty1<MongoQuestionnaire, Any?>) {
    TITLE(MongoQuestionnaire::title),
    AUTHOR_NAME(MongoQuestionnaire::authorInfo / AuthorInfo::userName),
    LAST_UPDATED(MongoQuestionnaire::lastModifiedTimestamp)
}