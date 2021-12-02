package com.quizappbackend.model.databases.mongodb

import com.quizappbackend.model.databases.dto.CourseOfStudiesIdWithTimeStamp
import com.quizappbackend.model.databases.dto.FacultyIdWithTimeStamp
import com.quizappbackend.model.databases.mongodb.dao.*
import com.quizappbackend.model.databases.mongodb.documents.DocumentMarker
import com.quizappbackend.model.databases.mongodb.documents.faculty.*
import com.quizappbackend.model.databases.mongodb.documents.questionnaire.MongoQuestionnaire
import com.quizappbackend.model.databases.QuestionnaireVisibility
import com.quizappbackend.model.databases.dto.BrowsableOrderBy
import com.quizappbackend.model.databases.dto.ManageUsersOrderBy
import com.quizappbackend.model.databases.mongodb.documents.questionnairefilled.MongoFilledQuestionnaire
import com.quizappbackend.model.databases.mongodb.documents.user.AuthorId
import com.quizappbackend.model.databases.mongodb.documents.user.AuthorInfo
import com.quizappbackend.model.databases.mongodb.documents.user.Role
import com.quizappbackend.model.databases.mongodb.documents.user.User
import com.quizappbackend.model.networking.requests.SyncQuestionnairesRequest
import com.quizappbackend.model.networking.responses.SyncQuestionnairesResponse
import com.quizappbackend.utils.Constants
import com.quizappbackend.utils.DataPrefillUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.bson.conversions.Bson
import org.litote.kmongo.*
import org.litote.kmongo.MongoOperator.*
import org.litote.kmongo.and
import org.litote.kmongo.coroutine.aggregate
import org.litote.kmongo.coroutine.projection
import java.time.Instant
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
            if (facultyDao.isCollectionEmpty() && courseOfStudiesDao.isCollectionEmpty()) {
                DataPrefillUtil.generateFacultiesAndCoursesOfStudies().let { (faculties, coursesOfStudies) ->
                    facultyDao.prefillFaculties(faculties)
                    courseOfStudiesDao.prefillCourseOfStudies(coursesOfStudies)
                }
            }
        }
    }

    //BaseDao
    suspend inline fun <reified T : DocumentMarker> getAllEntries() = getBaseDaoWith(T::class).getAllEntries()

    suspend inline fun <reified T : DocumentMarker> checkIfExistsWithId(id: String) = getBaseDaoWith(T::class).checkIfExistsWithId(id)

    suspend inline fun <reified T : DocumentMarker> insertOne(document: T) = getBaseDaoWith(T::class).insertOne(document)

    suspend inline fun <reified T : DocumentMarker> insertMany(documents: List<T>) = getBaseDaoWith(T::class).insertMany(documents)

    suspend inline fun <reified T : DocumentMarker> findOneById(id: String) = getBaseDaoWith(T::class).findOneById(id)

    suspend inline fun <reified T : DocumentMarker> findManyById(id: String, idField: KProperty1<T, String?>) = getBaseDaoWith(T::class).findManyById(id, idField)

    suspend inline fun <reified T : DocumentMarker> findManyByIds(ids: List<String>, idField: KProperty1<T, String?>) = getBaseDaoWith(T::class).findManyByIds(ids, idField)

    suspend inline fun <reified T : DocumentMarker> deleteOneById(id: String) = getBaseDaoWith(T::class).deleteOneById(id)

    suspend inline fun <reified T : DocumentMarker> deleteManyById(id: String, idField: KProperty1<T, String?>) = getBaseDaoWith(T::class).deleteManyById(id, idField)

    suspend inline fun <reified T : DocumentMarker> deleteManyByIds(ids: List<String>, idField: KProperty1<T, String?>) =
        getBaseDaoWith(T::class).deleteManyByIds(ids, idField)

    suspend inline fun <reified T : DocumentMarker> replaceOneById(entry: T, id: String, upsert: Boolean = false) =
        getBaseDaoWith(T::class).replaceOneById(entry, id, upsert)

    suspend inline fun <reified T : DocumentMarker> replaceOneWith(entry: T, upsert: Boolean, crossinline replaceByFilter: ((T) -> Bson)) =
        getBaseDaoWith(T::class).replaceOneWith(entry, upsert, replaceByFilter)

    suspend inline fun <reified T : DocumentMarker> replaceManyById(entries: List<T>, idField: KProperty<String?>, upsert: Boolean) =
        getBaseDaoWith(T::class).replaceManyById(entries, idField, upsert)

    suspend inline fun <reified T : DocumentMarker> replaceManyByFilter(entries: List<T>, upsert: Boolean, crossinline replaceByFilter: ((T) -> Bson)) =
        getBaseDaoWith(T::class).replaceManyByFilter(entries, upsert, replaceByFilter)

    suspend inline fun <reified T : DocumentMarker> updateOneById(id: String, newValue: T) = getBaseDaoWith(T::class).updateOneById(id, newValue)

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
    suspend fun checkIfUserExistsWithName(userName: String) = userDao.checkIfUserExistsWithName(userName)

    suspend fun getUserWithUserName(userName: String) = userDao.getUserWithUserName(userName)

    suspend fun changeUserName(userId: String, newUserName: String) = questionnaireDao.updateAuthorNameInQuestionnaires(userId, newUserName)
            && userDao.changeUserName(userId, newUserName)

    suspend fun deleteUser(userId: String) = deleteManyById(userId, MongoFilledQuestionnaire::userId)
            && deleteManyById(userId, MongoQuestionnaire::authorInfo / AuthorInfo::userId)
            && deleteOneById<User>(userId)

    suspend fun deleteUsers(userIds: List<String>) = deleteManyByIds(userIds, MongoFilledQuestionnaire::userId)
            && deleteManyByIds(userIds, MongoQuestionnaire::authorInfo / AuthorInfo::userId)
            && deleteManyByIds(userIds, User::id)

    suspend fun getUsersPaged(limit: Int, page: Int, searchQuery: String, roles: Set<Role>, orderBy: ManageUsersOrderBy, ascending: Boolean) =
        userDao.getUsersPaged(limit, page, searchQuery, roles, orderBy, ascending)

    suspend fun getAuthorsPaged(limit: Int, page: Int, searchQuery: String) = userDao.getAuthorsPaged(limit, page, searchQuery)

    suspend fun updateUserRole(userId: String, role: Role) = userDao.updateUserRole(userId, role)


    //QUESTIONNAIRE QUERIES
    suspend fun findQuestionnairesWith(filledQuestionnaires: List<MongoFilledQuestionnaire>) =
        findManyByIds(filledQuestionnaires.map(MongoFilledQuestionnaire::questionnaireId), MongoQuestionnaire::id)

    suspend fun getXAmountOfQuestionnaires(limit: Int) = questionnaireDao.getXAmountOfQuestionnaires(limit)

    suspend fun deleteQuestionnaires(questionnaireIds: List<String>) = deleteManyByIds(questionnaireIds, MongoFilledQuestionnaire::questionnaireId)
            && deleteManyByIds(questionnaireIds, MongoQuestionnaire::id)

    suspend fun changeQuestionnaireVisibility(questionnaireId: String, newVisibility: QuestionnaireVisibility) =
        questionnaireDao.changeQuestionnaireVisibility(questionnaireId, newVisibility)

    suspend fun insertOrReplaceQuestionnaire(mongoQuestionnaires: List<MongoQuestionnaire>) =
        replaceManyById(mongoQuestionnaires, MongoQuestionnaire::id, true)

    suspend fun getQuestionnairesPaged(
        userId: String,
        limit: Int,
        page: Int,
        searchQuery: String,
        questionnaireIdsToIgnore: List<String>,
        facultyIds: List<String>,
        courseOfStudiesIds: List<String>,
        authorIds: List<String>,
        browsableOrderBy: BrowsableOrderBy,
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
        browsableOrderBy = browsableOrderBy,
        ascending = ascending
    )

    suspend fun getQuestionnairesPagedBucket(
        userId: String,
        limit: Int,
        page: Int,
        searchQuery: String
    ) = questionnaireDao.getQuestionnairesPagedBucket(userId, limit, page, searchQuery)


    //FILLED QUESTIONNAIRE QUERIES
    suspend fun insertFilledQuestionnaireIfNotAlreadyPresent(mongoFilledQuestionnaire: MongoFilledQuestionnaire, userId: String) =
        filledQuestionnaireDao.insertFilledQuestionnaireIfNotAlreadyPresent(mongoFilledQuestionnaire.apply { this.userId = userId })

    suspend fun deleteFilledQuestionnairesForUser(userId: String, questionnaireIds: List<String>) =
        filledQuestionnaireDao.deleteFilledQuestionnairesForUser(userId, questionnaireIds)

    suspend fun replaceOneFilledQuestionnaireWith(mongoFilledQuestionnaire: MongoFilledQuestionnaire, userId: String) =
        filledQuestionnaireDao.replaceOneFilledQuestionnaireWith(mongoFilledQuestionnaire.apply { this.userId = userId })

    suspend fun replaceManyFilledQuestionnairesWith(mongoFilledQuestionnaires: List<MongoFilledQuestionnaire>, userId: String) =
        filledQuestionnaireDao.replaceManyFilledQuestionnairesWith(mongoFilledQuestionnaires.onEach { it.userId = userId })

    suspend fun getFilledQuestionnaire(userId: String, questionnaireId: String) = filledQuestionnaireDao.getFilledQuestionnaire(userId, questionnaireId)


    //FACULTY
    suspend fun generateSyncFacultiesResponse(facultyIdsWithTimeStamps: List<FacultyIdWithTimeStamp>) =
        facultyDao.generateSyncFacultiesResponse(facultyIdsWithTimeStamps)

    suspend fun insertOrReplaceFaculty(faculty: MongoFaculty) = replaceOneById(faculty, faculty.id, true)

    suspend fun deleteFacultyById(facultyId: String): Boolean {
        return deleteOneById<MongoFaculty>(facultyId).also { wasAcknowledged ->
            if (wasAcknowledged) {
                courseOfStudiesDao.collection.updateMany(
                    MongoCourseOfStudies::facultyIds contains facultyId,
                    pull(MongoCourseOfStudies::facultyIds, facultyId)
                )
//                questionnaireDao.collection.updateMany(
//                    MongoQuestionnaire::facultyIds contains facultyId,
//                    pull(MongoQuestionnaire::facultyIds, facultyId)
//                )
            }
        }
    }

    suspend fun isFacultyAbbreviationAlreadyUsed(faculty: MongoFaculty) = facultyDao.isFacultyAbbreviationAlreadyUsed(faculty)

    suspend fun isFacultyNameAlreadyUsed(faculty: MongoFaculty) = facultyDao.isFacultyNameAlreadyUsed(faculty)


    //COURSE OF STUDIES
    suspend fun generateSyncCoursesOfStudiesResponse(courseOfStudiesIdsWithTimeStamps: List<CourseOfStudiesIdWithTimeStamp>) =
        courseOfStudiesDao.generateSyncFacultiesResponse(courseOfStudiesIdsWithTimeStamps)

    suspend fun insertOrReplaceCourseOfStudies(courseOfStudies: MongoCourseOfStudies) = replaceOneById(courseOfStudies, courseOfStudies.id, true)

    suspend fun isCourseOfStudiesAbbreviationAlreadyUsed(courseOfStudies: MongoCourseOfStudies) = courseOfStudiesDao.isCourseOfStudiesAbbreviationAlreadyUsed(courseOfStudies)


    //NEW METHOD
    suspend fun generateSyncQuestionnairesResponse(userId: String, request: SyncQuestionnairesRequest): SyncQuestionnairesResponse = withContext(IO) {
        val filledQuestionnaires = getAllFilledQuestionnairesForUser(
            userId = userId,
            unsyncedQuestionnaireIds = request.unsyncedQuestionnaireIds,
            locallyDeletedQuestionnaireIds = request.locallyDeletedQuestionnaireIds
        )

        //ONLY DOWNLOAD FILLED QUESTIONNAIRES WHEN THERE IS NO QUESTIONNAIRE LOCALLY
        val filteredFilledQuestionnaires = async {
            filledQuestionnaires.filter { q ->
                request.syncedQuestionnaireIdsWithTimestamp.none { q.questionnaireId == it.id }
            }
        }

        val questionnaires = getAllQuestionnairesConnectedToUser(
            userId = userId,
            unsyncedQuestionnaireIds = request.unsyncedQuestionnaireIds,
            locallyDeletedQuestionnaireIds = request.locallyDeletedQuestionnaireIds,
            syncedQuestionnaireIds = request.syncedQuestionnaireIdsWithTimestamp.map { it.id },
            filledMongoQuestionnaireIds = filledQuestionnaires.map { it.questionnaireId })

        //CHECKS IF THERE ARE QUESTIONNAIRES LOCALLY WHICH ARE NOT PRESENT IN MONGO DB ANYMORE
        val questionnairesToSetStatusToUnsynced = async {
            request.syncedQuestionnaireIdsWithTimestamp.filter { qwt -> questionnaires.none { it.id == qwt.id } }.map { it.id }
        }

        //CHECK IF THERE WAS AN UPDATE TO THE QUESTIONNAIRE
        // -> IF YES DOWNLOAD THE SAID QUESTIONNAIRE
        // -> IF NO IGNORE THE SAID QUESTIONNAIRE
        val filteredQuestionnaires = async {
            questionnaires.filter { q ->
                request.syncedQuestionnaireIdsWithTimestamp.firstOrNull { q.id == it.id }?.let {
                    it.lastModifiedTimestamp != q.lastModifiedTimestamp
                } ?: true
            }
        }

        return@withContext SyncQuestionnairesResponse(
            mongoQuestionnaires = filteredQuestionnaires.await(),
            mongoFilledQuestionnaires = filteredFilledQuestionnaires.await(),
            questionnaireIdsToUnsync = questionnairesToSetStatusToUnsynced.await()
        )
    }

    private suspend fun getAllFilledQuestionnairesForUser(
        userId: String,
        unsyncedQuestionnaireIds: List<String>,
        locallyDeletedQuestionnaireIds: List<String>
    ): List<MongoFilledQuestionnaire> = filledQuestionnaireDao.getAllFilledQuestionnairesForUser(
        userId = userId,
        questionnairesToIgnore = unsyncedQuestionnaireIds + locallyDeletedQuestionnaireIds
    )

    private suspend fun getAllQuestionnairesConnectedToUser(
        userId: String,
        unsyncedQuestionnaireIds: List<String>,
        locallyDeletedQuestionnaireIds: List<String>,
        syncedQuestionnaireIds: List<String>,
        filledMongoQuestionnaireIds: List<String>
    ): List<MongoQuestionnaire> = questionnaireDao.getAllQuestionnairesConnectedToUser(
        userId = userId,
        questionnairesToIgnore = unsyncedQuestionnaireIds + locallyDeletedQuestionnaireIds,
        questionnairesToFind = syncedQuestionnaireIds + filledMongoQuestionnaireIds - locallyDeletedQuestionnaireIds.toSet()
    )















    data class Answer(val evaluator: String, val alreadyUsed: Boolean, val answerDate: Instant)

    data class EvaluationsForms(val questions: List<String>)

    data class EvaluationsFormsWithResults(val questions: List<String>, val results: List<EvaluationRequest>)

    data class EvaluationsAnswers(val questionId: String, val evaluated: String, val answers: List<Answer>)

    data class EvaluationRequest(val userId: String, val evaluationDate: String)


    suspend fun findUnconnectedQuestionnaires(): List<Any> {

        return  questionnaireDao.collection
            .aggregate<Any>(
                lookup(
                    from = "Users",
                    localField = "authorInfo.userId",
                    foreignField = "_id",
                    newAs = "connection"
                ),
                unwind("\$connection")
            ).toList()


        val userIdsInQuestionnaires = questionnaireDao.collection
            .distinct(MongoQuestionnaire::authorInfo)
            .toList()
            .map(AuthorInfo::userId)

        val allUserIds =  userDao.collection
            .withDocumentClass<AuthorId>()
            .projection(User::id)
            .toList()

        val diff = userIdsInQuestionnaires - allUserIds.toSet()

        return questionnaireDao.collection
            .find(MongoQuestionnaire::authorInfo / AuthorInfo::userId `in` diff)
            .toList()
    }
}