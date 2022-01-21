package com.quizappbackend.model.mongodb.dao

import com.quizappbackend.model.mongodb.documents.MongoQuestionnaire
import com.quizappbackend.model.mongodb.dto.MongoBrowsableQuestionnaire
import com.quizappbackend.model.mongodb.dto.RemoteQuestionnaireOrderBy
import com.quizappbackend.model.mongodb.properties.QuestionnaireVisibility

interface QuestionnaireDao: BaseDao<MongoQuestionnaire> {

    suspend fun updateQuestionnaireVisibility(questionnaireId: String, newVisibility: QuestionnaireVisibility) : Boolean

    suspend fun updateAuthorNameOfQuestionnaires(userId: String, newUserName: String) : Boolean

    suspend fun getAllQuestionnairesConnectedToUser(
        userId: String,
        questionnairesToIgnore: List<String>,
        questionnairesToFind: List<String>
    ) : List<MongoQuestionnaire>

    suspend fun getQuestionnairesPaged(
        userId: String,
        limit: Int,
        page: Int,
        searchQuery: String,
        questionnaireIdsToIgnore: List<String>,
        facultyIds: List<String>,
        courseOfStudiesIds: List<String>,
        authorIds: List<String>,
        orderBy: RemoteQuestionnaireOrderBy,
        ascending: Boolean
    ): List<MongoBrowsableQuestionnaire>

    suspend fun getXAmountOfQuestionnaires(limit: Int) : List<MongoQuestionnaire>

    suspend fun removeFacultyFromQuestionnaire(facultyId: String) : Boolean

    suspend fun removeCourseOfStudiesFromQuestionnaire(courseOfStudiesId: String): Boolean

}