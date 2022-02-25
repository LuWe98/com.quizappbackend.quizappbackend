package com.quizappbackend.model.mongodb

import com.quizappbackend.model.ktor.BackendRequest
import com.quizappbackend.model.ktor.paging.BrowsableQuestionnairePageKeys
import com.quizappbackend.model.mongodb.documents.*
import com.quizappbackend.model.mongodb.dto.CourseOfStudiesIdWithTimeStamp
import com.quizappbackend.model.mongodb.dto.FacultyIdWithTimeStamp
import com.quizappbackend.model.mongodb.dto.MongoBrowsableQuestionnaire
import com.quizappbackend.model.mongodb.properties.AuthorInfo
import com.quizappbackend.model.mongodb.properties.QuestionnaireVisibility
import com.quizappbackend.model.mongodb.properties.Role
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

interface MongoRepository {

    suspend fun <T : DocumentMarker> isCollectionEmpty(clazz: KClass<T>): Boolean

    suspend fun <T : DocumentMarker> getAllEntries(clazz: KClass<T>): List<T>

    suspend fun <T : DocumentMarker> insertOne(document: T, clazz: KClass<T>): Boolean

    suspend fun <T : DocumentMarker> insertMany(documents: List<T>, clazz: KClass<T>): Boolean

    suspend fun <T : DocumentMarker> findOneById(id: String, clazz: KClass<T>): T?

    suspend fun <T : DocumentMarker> findManyByIds(ids: List<String>, idField: KProperty1<T, String?>, clazz: KClass<T>): List<T>

    suspend fun <T : DocumentMarker> deleteOneById(id: String, clazz: KClass<T>): Boolean

    suspend fun <T : DocumentMarker> deleteManyById(id: String, idField: KProperty1<T, String?>, clazz: KClass<T>): Boolean

    suspend fun <T : DocumentMarker> deleteManyByIds(ids: List<String>, idField: KProperty1<T, String?>, clazz: KClass<T>): Boolean


    //USER QUERIES
    suspend fun checkIfUserExistsWithName(userName: String): Boolean

    suspend fun findUserByName(userName: String): User?

    suspend fun deleteUser(userId: String): Boolean


    suspend fun updateUserRole(userId: String, role: Role): Boolean

    suspend fun updateUserCanShareQuestionnaireWith(userId: String, canShare: Boolean): Boolean

    suspend fun updateUserPassword(userId: String, newPassword: String): Boolean

    suspend fun getPagedAuthors(limit: Int, page: Int, searchQuery: String): List<AuthorInfo>

    suspend fun getPagedUsers(request: BackendRequest.GetPagedUserAdminRequest): List<User>


    //QUESTIONNAIRE QUERIES
    suspend fun doesQuestionnaireExist(questionnaireId: String): Boolean

    suspend fun findQuestionnairesWith(filledQuestionnaires: List<MongoFilledQuestionnaire>): List<MongoQuestionnaire>

    suspend fun deleteQuestionnaires(questionnaireIds: List<String>): Boolean

    suspend fun changeQuestionnaireVisibility(questionnaireId: String, newVisibility: QuestionnaireVisibility): Boolean

    suspend fun insertOrReplaceQuestionnaires(mongoQuestionnaires: List<MongoQuestionnaire>): Boolean

    suspend fun getAllQuestionnairesConnectedToUser(userId: String, questionnairesToIgnore: List<String>, questionnairesToFind: List<String>): List<MongoQuestionnaire>

    suspend fun getQuestionnairesPaged(userId: String, request: BackendRequest.GetPagedQuestionnairesRequest): List<MongoBrowsableQuestionnaire>

    suspend fun getPreviousPageKeys(userId: String, request: BackendRequest.GetPagedQuestionnairesRequest): BrowsableQuestionnairePageKeys?


    //FILLED QUESTIONNAIRE QUERIES
    suspend fun insertFilledQuestionnaireIfNotPresent(userId: String, mongoFilledQuestionnaire: MongoFilledQuestionnaire): Boolean

    suspend fun deleteFilledQuestionnairesForUser(userId: String, questionnaireIds: List<String>): Boolean

    suspend fun replaceFilledQuestionnaireWith(userId: String, mongoFilledQuestionnaire: MongoFilledQuestionnaire): Boolean

    suspend fun replaceFilledQuestionnairesWith(userId: String, mongoFilledQuestionnaires: List<MongoFilledQuestionnaire>): Boolean

    suspend fun findFilledQuestionnaire(userId: String, questionnaireId: String): MongoFilledQuestionnaire?

    suspend fun findFilledQuestionnairesForUser(userId: String, questionnairesToIgnore: List<String>): List<MongoFilledQuestionnaire>


    //FACULTY
    suspend fun insertOrReplaceFaculty(faculty: MongoFaculty): Boolean

    suspend fun findFacultyIdsToDeleteLocally(localFacultyIdsWithTimestamp: List<FacultyIdWithTimeStamp>): List<String>

    suspend fun findFacultiesNotUpToDateOfUser(localFacultyIdsWithTimestamp: List<FacultyIdWithTimeStamp>): List<MongoFaculty>

    suspend fun isFacultyAbbreviationAlreadyUsed(faculty: MongoFaculty): Boolean

    suspend fun isFacultyNameAlreadyUsed(faculty: MongoFaculty): Boolean

    suspend fun deleteFacultyById(facultyId: String): Boolean


    //COURSE OF STUDIES
    suspend fun findCourseOfStudiesIdsToDeleteLocally(localCourseOfStudiesIdsWithTimeStamp: List<CourseOfStudiesIdWithTimeStamp>): List<String>

    suspend fun findCoursesOfStudiesNotUpToDateOfUser(localCourseOfStudiesIdsWithTimeStamp: List<CourseOfStudiesIdWithTimeStamp>): List<MongoCourseOfStudies>

    suspend fun insertOrReplaceCourseOfStudies(courseOfStudies: MongoCourseOfStudies): Boolean

    suspend fun isCourseOfStudiesAbbreviationAlreadyUsed(courseOfStudies: MongoCourseOfStudies): Boolean

    suspend fun deleteCourseOfStudiesById(courseOfStudiesId: String): Boolean


    //TEST
    suspend fun getXAmountOfQuestionnaires(limit: Int): List<MongoQuestionnaire>

}