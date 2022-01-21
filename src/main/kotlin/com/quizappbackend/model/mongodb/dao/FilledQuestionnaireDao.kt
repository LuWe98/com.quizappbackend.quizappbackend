package com.quizappbackend.model.mongodb.dao

import com.quizappbackend.model.mongodb.documents.MongoFilledQuestionnaire

interface FilledQuestionnaireDao: BaseDao<MongoFilledQuestionnaire> {

    suspend fun insertFilledQuestionnaireIfNotPresent(mongoFilledQuestionnaire: MongoFilledQuestionnaire): Boolean

    suspend fun deleteFilledQuestionnairesForUser(userId: String, questionnaireIds: List<String>): Boolean

    suspend fun findFilledQuestionnaire(userId: String, questionnaireId: String): MongoFilledQuestionnaire?

    suspend fun findFilledQuestionnairesForUser(userId: String, questionnairesToIgnore: List<String>): List<MongoFilledQuestionnaire>

    suspend fun replaceFilledQuestionnaireWith(mongoFilledQuestionnaire: MongoFilledQuestionnaire): Boolean

    suspend fun replaceFilledQuestionnairesWith(mongoFilledQuestionnaires: List<MongoFilledQuestionnaire>): Boolean

}