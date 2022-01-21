package com.quizappbackend.model.mongodb

import com.quizappbackend.model.mongodb.dao.*
import com.quizappbackend.model.mongodb.documents.*
import com.quizappbackend.model.mongodb.dto.CourseOfStudiesIdWithTimeStamp
import com.quizappbackend.model.mongodb.dto.FacultyIdWithTimeStamp
import com.quizappbackend.model.mongodb.dto.ManageUsersOrderBy
import com.quizappbackend.model.mongodb.dto.RemoteQuestionnaireOrderBy
import com.quizappbackend.model.mongodb.properties.AuthorInfo
import com.quizappbackend.model.mongodb.properties.QuestionnaireVisibility
import com.quizappbackend.model.mongodb.properties.Role
import com.quizappbackend.utils.DataPrefillUtil
import io.ktor.util.date.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import org.litote.kmongo.*
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1

class MongoRepository(
    private val userDao: UserDao,
    private val questionnaireDao: QuestionnaireDao,
    private val filledQuestionnaireDao: FilledQuestionnaireDao,
    private val facultyDao: FacultyDao,
    private val courseOfStudiesDao: CourseOfStudiesDao
) {

    init {
        CoroutineScope(IO).launch {
            if (isCollectionEmpty<MongoFaculty>() && isCollectionEmpty<MongoCourseOfStudies>()) {
                DataPrefillUtil.facultiesAndCoursesOfStudies.let { (faculties, coursesOfStudies) ->
                    insertMany(faculties)
                    insertMany(coursesOfStudies)
                }
            }

            if (isCollectionEmpty<User>()) {
                insertMany(DataPrefillUtil.professorListShortened)
            }
        }
    }

    //BaseDao
    private suspend inline fun <reified T : DocumentMarker> isCollectionEmpty() =
        getBaseDaoWith(T::class).isCollectionEmpty()

    suspend inline fun <reified T : DocumentMarker> getAllEntries() =
        getBaseDaoWith(T::class).getAllEntries()

    suspend inline fun <reified T : DocumentMarker> insertOne(document: T) =
        getBaseDaoWith(T::class).insertOne(document)

    suspend inline fun <reified T : DocumentMarker> insertMany(documents: List<T>) =
        getBaseDaoWith(T::class).insertMany(documents)

    suspend inline fun <reified T : DocumentMarker> findOneById(id: String) =
        getBaseDaoWith(T::class).findOneById(id)

    private suspend inline fun <reified T : DocumentMarker> findManyByIds(ids: List<String>, idField: KProperty1<T, String?>) =
        getBaseDaoWith(T::class).findManyByIds(ids, idField)

    private suspend inline fun <reified T : DocumentMarker> deleteOneById(id: String) =
        getBaseDaoWith(T::class).deleteOneById(id)

    suspend inline fun <reified T : DocumentMarker> deleteManyById(id: String, idField: KProperty1<T, String?>) =
        getBaseDaoWith(T::class).deleteManyById(id, idField)

    suspend inline fun <reified T : DocumentMarker> deleteManyByIds(ids: List<String>, idField: KProperty1<T, String?>) =
        getBaseDaoWith(T::class).deleteManyByIds(ids, idField)

    private suspend inline fun <reified T : DocumentMarker> replaceOneById(entry: T, id: String, upsert: Boolean = false) =
        getBaseDaoWith(T::class).replaceOneById(entry, id, upsert)

    private suspend inline fun <reified T : DocumentMarker> replaceManyById(entries: List<T>, idField: KProperty<String?>, upsert: Boolean) =
        getBaseDaoWith(T::class).replaceManyById(entries, idField, upsert)

    @Suppress("UNCHECKED_CAST")
    fun <T : DocumentMarker> getBaseDaoWith(documentClass: KClass<T>) = (when (documentClass) {
        User::class -> userDao
        MongoQuestionnaire::class -> questionnaireDao
        MongoFilledQuestionnaire::class -> filledQuestionnaireDao
        MongoFaculty::class -> facultyDao
        MongoCourseOfStudies::class -> courseOfStudiesDao
        else -> throw IllegalArgumentException()
    } as BaseDao<T>)





    //USER QUERIES
    suspend fun checkIfUserExistsWithName(userName: String) = userDao.findUserByName(userName) != null

    suspend fun findUserByName(userName: String) = userDao.findUserByName(userName)

    suspend fun updateUserName(userId: String, newUserName: String) = questionnaireDao.updateAuthorNameOfQuestionnaires(userId, newUserName)
            && userDao.updateUserName(userId, newUserName)

    suspend fun deleteUser(userId: String) = deleteManyById(userId, MongoFilledQuestionnaire::userId)
            && deleteManyById(userId, MongoQuestionnaire::authorInfo / AuthorInfo::userId)
            && deleteOneById<User>(userId)

    suspend fun updateUserRole(userId: String, role: Role) = userDao.updateUserRole(userId, role)

    suspend fun updateUserPassword(userId: String, newPassword: String) = userDao.updateUserPassword(userId, newPassword)

    suspend fun getAuthorsPaged(limit: Int, page: Int, searchQuery: String) = userDao.getAuthorsPaged(limit, page, searchQuery)

    suspend fun getUsersPaged(
        limit: Int,
        page: Int,
        searchQuery: String,
        roles: Set<Role>,
        orderBy: ManageUsersOrderBy,
        ascending: Boolean
    ) = userDao.getUsersPaged(
        limit,
        page,
        searchQuery,
        roles,
        orderBy,
        ascending
    )




    //QUESTIONNAIRE QUERIES
    suspend fun findQuestionnairesWith(filledQuestionnaires: List<MongoFilledQuestionnaire>) =
        findManyByIds(filledQuestionnaires.map(MongoFilledQuestionnaire::questionnaireId), MongoQuestionnaire::id)

    suspend fun getXAmountOfQuestionnaires(limit: Int) = questionnaireDao.getXAmountOfQuestionnaires(limit)

    suspend fun deleteQuestionnaires(questionnaireIds: List<String>) = deleteManyByIds(questionnaireIds, MongoFilledQuestionnaire::questionnaireId)
            && deleteManyByIds(questionnaireIds, MongoQuestionnaire::id)

    suspend fun changeQuestionnaireVisibility(questionnaireId: String, newVisibility: QuestionnaireVisibility) =
        questionnaireDao.updateQuestionnaireVisibility(questionnaireId, newVisibility)

    suspend fun insertOrReplaceQuestionnaire(mongoQuestionnaires: List<MongoQuestionnaire>) =
        replaceManyById(mongoQuestionnaires, MongoQuestionnaire::id, true)

    suspend fun getAllQuestionnairesConnectedToUser(userId: String, questionnairesToIgnore: List<String>, questionnairesToFind: List<String>) =
        questionnaireDao.getAllQuestionnairesConnectedToUser(userId, questionnairesToIgnore, questionnairesToFind)

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
    ) = questionnaireDao.getQuestionnairesPaged(
        userId = userId,
        limit = limit,
        page = page,
        searchQuery = searchQuery,
        questionnaireIdsToIgnore = questionnaireIdsToIgnore,
        facultyIds = facultyIds,
        courseOfStudiesIds = courseOfStudiesIds,
        authorIds = authorIds,
        orderBy = orderBy,
        ascending = ascending
    )




    //FILLED QUESTIONNAIRE QUERIES
    suspend fun insertFilledQuestionnaireIfNotPresent(userId: String, mongoFilledQuestionnaire: MongoFilledQuestionnaire) =
        filledQuestionnaireDao.insertFilledQuestionnaireIfNotPresent(mongoFilledQuestionnaire.copy(userId = userId))

    suspend fun deleteFilledQuestionnairesForUser(userId: String, questionnaireIds: List<String>) =
        filledQuestionnaireDao.deleteFilledQuestionnairesForUser(userId, questionnaireIds)

    suspend fun replaceFilledQuestionnaireWith(userId: String, mongoFilledQuestionnaire: MongoFilledQuestionnaire) =
        filledQuestionnaireDao.replaceFilledQuestionnaireWith(mongoFilledQuestionnaire.copy(userId = userId))

    suspend fun replaceFilledQuestionnairesWith(userId: String, mongoFilledQuestionnaires: List<MongoFilledQuestionnaire>) =
        filledQuestionnaireDao.replaceFilledQuestionnairesWith(mongoFilledQuestionnaires.map { it.copy(userId = userId) })

    suspend fun findFilledQuestionnaire(userId: String, questionnaireId: String) = filledQuestionnaireDao.findFilledQuestionnaire(userId, questionnaireId)

    suspend fun findFilledQuestionnairesForUser(userId: String, questionnairesToIgnore: List<String>) =
        filledQuestionnaireDao.findFilledQuestionnairesForUser(userId, questionnairesToIgnore)



    //FACULTY
    suspend fun insertOrReplaceFaculty(faculty: MongoFaculty) = replaceOneById(faculty, faculty.id, true)

    suspend fun findFacultiesToDeleteLocally(localFacultyIdsWithTimestamp: List<FacultyIdWithTimeStamp>) =
        facultyDao.findFacultiesToDeleteLocally(localFacultyIdsWithTimestamp)

    suspend fun findFacultiesNotUpToDateOfUser(localFacultyIdsWithTimestamp: List<FacultyIdWithTimeStamp>) =
        facultyDao.findFacultiesNotUpToDateOfUser(localFacultyIdsWithTimestamp)

    //TODO -> Der kann noch weg
    suspend fun findFacultyByAbbreviation(abbreviation: String) = facultyDao.findFacultyByAbbreviation(abbreviation)

    suspend fun isFacultyAbbreviationAlreadyUsed(faculty: MongoFaculty) = facultyDao.isFacultyAbbreviationAlreadyUsed(faculty)

    suspend fun isFacultyNameAlreadyUsed(faculty: MongoFaculty) = facultyDao.isFacultyNameAlreadyUsed(faculty)

    suspend fun deleteFacultyById(facultyId: String): Boolean {
        return deleteOneById<MongoFaculty>(facultyId).also { wasAcknowledged ->
            if (wasAcknowledged) {
                courseOfStudiesDao.removeFacultyFromCourseOfStudies(facultyId)
                questionnaireDao.removeFacultyFromQuestionnaire(facultyId)
            }
        }
    }



    //COURSE OF STUDIES
    suspend fun findCourseOfStudiesIdsToDeleteLocally(localCourseOfStudiesIdsWithTimeStamp: List<CourseOfStudiesIdWithTimeStamp>) =
        courseOfStudiesDao.findCourseOfStudiesToDeleteLocally(localCourseOfStudiesIdsWithTimeStamp)

    suspend fun findCoursesOfStudiesNotUpToDateOfUser(localCourseOfStudiesIdsWithTimeStamp: List<CourseOfStudiesIdWithTimeStamp>) =
        courseOfStudiesDao.findCourseOfStudiesNotUpToDateOfUser(localCourseOfStudiesIdsWithTimeStamp)

    suspend fun insertOrReplaceCourseOfStudies(courseOfStudies: MongoCourseOfStudies) = replaceOneById(courseOfStudies, courseOfStudies.id, true)

    suspend fun isCourseOfStudiesAbbreviationAlreadyUsed(courseOfStudies: MongoCourseOfStudies) = courseOfStudiesDao.isCourseOfStudiesAbbreviationAlreadyUsed(courseOfStudies)

    suspend fun findCourseOfStudiesByFacultyId(facultyId: String) = courseOfStudiesDao.findCourseOfStudiesByFacultyId(facultyId)

    suspend fun deleteCourseOfStudiesById(courseOfStudiesId: String) : Boolean {
        return deleteOneById<MongoCourseOfStudies>(courseOfStudiesId).also { wasAcknowledged ->
            if(wasAcknowledged) {
                questionnaireDao.removeCourseOfStudiesFromQuestionnaire(courseOfStudiesId)
            }
        }
    }




//    suspend fun findUnconnectedQuestionnaires(): List<Any> {
//
//        return  questionnaireDao.collection
//            .aggregate<Any>(
//                lookup(
//                    from = "Users",
//                    localField = "authorInfo.userId",
//                    foreignField = "_id",
//                    newAs = "connection"
//                ),
//                unwind("\$connection")
//            ).toList()
//
//
//        val userIdsInQuestionnaires = questionnaireDao.collection
//            .distinct(MongoQuestionnaire::authorInfo)
//            .toList()
//            .map(AuthorInfo::userId)
//
//        val allUserIds =  userDao.collection
//            .withDocumentClass<AuthorId>()
//            .projection(User::id)
//            .toList()
//
//        val diff = userIdsInQuestionnaires - allUserIds.toSet()
//
//        return questionnaireDao.collection
//            .find(MongoQuestionnaire::authorInfo / AuthorInfo::userId `in` diff)
//            .toList()
//    }
}