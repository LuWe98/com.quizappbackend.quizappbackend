package com.quizappbackend.model.databases.dto

import com.quizappbackend.model.databases.mongodb.documents.questionnaire.MongoQuestionnaire
import com.quizappbackend.model.databases.mongodb.documents.user.AuthorInfo
import org.litote.kmongo.div
import kotlin.reflect.KProperty1

enum class BrowsableOrderBy(val orderByProperty: KProperty1<MongoQuestionnaire, Any?>) {
    TITLE(MongoQuestionnaire::title),
    AUTHOR_NAME(MongoQuestionnaire::authorInfo / AuthorInfo::userName),
    LAST_UPDATED(MongoQuestionnaire::lastModifiedTimestamp)
}