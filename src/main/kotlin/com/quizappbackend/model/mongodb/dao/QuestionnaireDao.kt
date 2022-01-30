package com.quizappbackend.model.mongodb.dao

import com.quizappbackend.model.ktor.BackendRequest
import com.quizappbackend.model.ktor.paging.BrowsableQuestionnairePageKeys
import com.quizappbackend.model.mongodb.documents.MongoQuestionnaire
import com.quizappbackend.model.mongodb.dto.MongoBrowsableQuestionnaire
import com.quizappbackend.model.mongodb.properties.QuestionnaireVisibility

interface QuestionnaireDao : BaseDao<MongoQuestionnaire> {

    suspend fun updateQuestionnaireVisibility(questionnaireId: String, newVisibility: QuestionnaireVisibility): Boolean

    suspend fun updateAuthorNameOfQuestionnaires(userId: String, newUserName: String): Boolean

    suspend fun getAllQuestionnairesConnectedToUser(
        userId: String,
        questionnairesToIgnore: List<String>,
        questionnairesToFind: List<String>
    ): List<MongoQuestionnaire>

    suspend fun getQuestionnairesPaged(
        userId: String,
        request: BackendRequest.GetPagedQuestionnairesRequest
    ): List<MongoBrowsableQuestionnaire>

    suspend fun getXAmountOfQuestionnaires(limit: Int): List<MongoQuestionnaire>

    suspend fun removeFacultyFromQuestionnaire(facultyId: String): Boolean

    suspend fun removeCourseOfStudiesFromQuestionnaire(courseOfStudiesId: String): Boolean





    suspend fun getQuestionnairesPagedWithPageKeys(
        userId: String,
        request: BackendRequest.GetPagedQuestionnairesWithPageKeysRequest
    ): List<MongoBrowsableQuestionnaire>

    suspend fun getPreviousPageKeys(
        userId: String,
        request: BackendRequest.GetPagedQuestionnairesWithPageKeysRequest
    ): BrowsableQuestionnairePageKeys?

}