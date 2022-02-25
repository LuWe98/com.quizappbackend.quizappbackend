package com.quizappbackend.model.mongodb

import com.quizappbackend.extensions.*
import com.quizappbackend.model.ktor.BackendRequest
import com.quizappbackend.model.mongodb.dao.*
import com.quizappbackend.model.mongodb.documents.*
import com.quizappbackend.model.mongodb.dto.CourseOfStudiesIdWithTimeStamp
import com.quizappbackend.model.mongodb.dto.FacultyIdWithTimeStamp
import com.quizappbackend.model.mongodb.properties.AuthorInfo
import com.quizappbackend.model.mongodb.properties.QuestionnaireVisibility
import com.quizappbackend.model.mongodb.properties.Role
import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.div
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

class MongoRepositoryImpl(
    private val client: CoroutineClient,
    private val userDao: UserDao,
    private val questionnaireDao: QuestionnaireDao,
    private val filledQuestionnaireDao: FilledQuestionnaireDao,
    private val facultyDao: FacultyDao,
    private val courseOfStudiesDao: CourseOfStudiesDao
) : MongoRepository {

//    suspend fun deleteUserInTransaction(userId: String) = client.runTransaction { session ->
//        filledQuestionnaireDao.collection.deleteMany(session, MongoFilledQuestionnaire::userId eq userId).wasAcknowledged()
//        questionnaireDao.collection.deleteMany(session, MongoQuestionnaire::authorInfo / AuthorInfo::userId eq userId).wasAcknowledged()
//        userDao.collection.deleteOne(session, User::id eq userId).wasAcknowledged()
//    }

    override suspend fun <T : DocumentMarker> isCollectionEmpty(clazz: KClass<T>) = getBaseDaoWith(clazz).isCollectionEmpty()

    override suspend fun <T : DocumentMarker> getAllEntries(clazz: KClass<T>) = getBaseDaoWith(clazz).getAllEntries()

    override suspend fun <T : DocumentMarker> insertOne(document: T, clazz: KClass<T>) = getBaseDaoWith(clazz).insertOne(document)

    override suspend fun <T : DocumentMarker> insertMany(documents: List<T>, clazz: KClass<T>) = getBaseDaoWith(clazz).insertMany(documents)

    override suspend fun <T : DocumentMarker> findOneById(id: String, clazz: KClass<T>) = getBaseDaoWith(clazz).findOneById(id)

    override suspend fun <T : DocumentMarker> findManyByIds(ids: List<String>, idField: KProperty1<T, String?>, clazz: KClass<T>) =
        getBaseDaoWith(clazz).findManyByIds(ids, idField)

    override suspend fun <T : DocumentMarker> deleteOneById(id: String, clazz: KClass<T>) = getBaseDaoWith(clazz).deleteOneById(id)

    override suspend fun <T : DocumentMarker> deleteManyById(id: String, idField: KProperty1<T, String?>, clazz: KClass<T>) =
        getBaseDaoWith(clazz).deleteManyById(id, idField)

    override suspend fun <T : DocumentMarker> deleteManyByIds(ids: List<String>, idField: KProperty1<T, String?>, clazz: KClass<T>) =
        getBaseDaoWith(clazz).deleteManyByIds(ids, idField)

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
    override suspend fun checkIfUserExistsWithName(userName: String) = userDao.findUserByName(userName) != null

    override suspend fun findUserByName(userName: String) = userDao.findUserByName(userName)

    override suspend fun deleteUser(userId: String) = deleteManyById(userId, MongoFilledQuestionnaire::userId)
            && deleteManyById(userId, MongoQuestionnaire::authorInfo / AuthorInfo::userId)
            && deleteOneById<User>(userId)


    override suspend fun updateUserRole(userId: String, role: Role) = userDao.updateUserRole(userId, role)

    override suspend fun updateUserCanShareQuestionnaireWith(userId: String, canShare: Boolean) = userDao.updateUserCanShareQuestionnaireWith(userId, canShare)

    override suspend fun updateUserPassword(userId: String, newPassword: String) = userDao.updateUserPassword(userId, newPassword)

    override suspend fun getPagedAuthors(limit: Int, page: Int, searchQuery: String) = userDao.getPagedAuthors(limit, page, searchQuery)

    override suspend fun getPagedUsers(request: BackendRequest.GetPagedUserAdminRequest) = userDao.getPagedUsers(request)


    //QUESTIONNAIRE QUERIES
    override suspend fun doesQuestionnaireExist(questionnaireId: String) = findOneById<MongoQuestionnaire>(questionnaireId) != null

    override suspend fun findQuestionnairesWith(filledQuestionnaires: List<MongoFilledQuestionnaire>) =
        findManyByIds(filledQuestionnaires.map(MongoFilledQuestionnaire::questionnaireId), MongoQuestionnaire::id)

    override suspend fun getXAmountOfQuestionnaires(limit: Int) = questionnaireDao.getXAmountOfQuestionnaires(limit)

    override suspend fun deleteQuestionnaires(questionnaireIds: List<String>) = deleteManyByIds(questionnaireIds, MongoFilledQuestionnaire::questionnaireId)
            && deleteManyByIds(questionnaireIds, MongoQuestionnaire::id)

    override suspend fun changeQuestionnaireVisibility(questionnaireId: String, newVisibility: QuestionnaireVisibility) =
        questionnaireDao.updateQuestionnaireVisibility(questionnaireId, newVisibility)

    override suspend fun insertOrReplaceQuestionnaires(mongoQuestionnaires: List<MongoQuestionnaire>) = questionnaireDao.insertOrReplaceQuestionnaires(mongoQuestionnaires)

    override suspend fun getAllQuestionnairesConnectedToUser(userId: String, questionnairesToIgnore: List<String>, questionnairesToFind: List<String>) =
        questionnaireDao.getAllQuestionnairesConnectedToUser(userId, questionnairesToIgnore, questionnairesToFind)

    override suspend fun getQuestionnairesPaged(userId: String, request: BackendRequest.GetPagedQuestionnairesRequest) =
        questionnaireDao.getQuestionnairesPaged(userId, request)

    override suspend fun getPreviousPageKeys(userId: String, request: BackendRequest.GetPagedQuestionnairesRequest) =
        questionnaireDao.getQuestionnaireRefreshKeys(userId, request)


    //FILLED QUESTIONNAIRE QUERIES
    override suspend fun insertFilledQuestionnaireIfNotPresent(userId: String, mongoFilledQuestionnaire: MongoFilledQuestionnaire) =
        filledQuestionnaireDao.insertFilledQuestionnaireIfNotPresent(mongoFilledQuestionnaire.copy(userId = userId))

    override suspend fun deleteFilledQuestionnairesForUser(userId: String, questionnaireIds: List<String>) =
        filledQuestionnaireDao.deleteFilledQuestionnairesForUser(userId, questionnaireIds)

    override suspend fun replaceFilledQuestionnaireWith(userId: String, mongoFilledQuestionnaire: MongoFilledQuestionnaire) =
        filledQuestionnaireDao.replaceFilledQuestionnaireWith(mongoFilledQuestionnaire.copy(userId = userId))

    override suspend fun replaceFilledQuestionnairesWith(userId: String, mongoFilledQuestionnaires: List<MongoFilledQuestionnaire>) =
        filledQuestionnaireDao.replaceFilledQuestionnairesWith(mongoFilledQuestionnaires.map { it.copy(userId = userId) })

    override suspend fun findFilledQuestionnaire(userId: String, questionnaireId: String) = filledQuestionnaireDao.findFilledQuestionnaire(userId, questionnaireId)

    override suspend fun findFilledQuestionnairesForUser(userId: String, questionnairesToIgnore: List<String>) =
        filledQuestionnaireDao.findFilledQuestionnairesForUser(userId, questionnairesToIgnore)


    //FACULTY
    override suspend fun insertOrReplaceFaculty(faculty: MongoFaculty) = facultyDao.insertOrReplaceFaculty(faculty, faculty.id, true)

    override suspend fun findFacultyIdsToDeleteLocally(localFacultyIdsWithTimestamp: List<FacultyIdWithTimeStamp>) =
        facultyDao.findFacultyIdsToDeleteLocally(localFacultyIdsWithTimestamp)

    override suspend fun findFacultiesNotUpToDateOfUser(localFacultyIdsWithTimestamp: List<FacultyIdWithTimeStamp>) =
        facultyDao.findFacultiesNotUpToDateOfUser(localFacultyIdsWithTimestamp)

    override suspend fun isFacultyAbbreviationAlreadyUsed(faculty: MongoFaculty) = facultyDao.isFacultyAbbreviationAlreadyUsed(faculty)

    override suspend fun isFacultyNameAlreadyUsed(faculty: MongoFaculty) = facultyDao.isFacultyNameAlreadyUsed(faculty)

    override suspend fun deleteFacultyById(facultyId: String) = deleteOneById<MongoFaculty>(facultyId).also { wasAcknowledged ->
        if (wasAcknowledged) {
            courseOfStudiesDao.removeFacultyFromCourseOfStudies(facultyId)
            questionnaireDao.removeFacultyFromQuestionnaire(facultyId)
        }
    }


    //COURSE OF STUDIES
    override suspend fun findCourseOfStudiesIdsToDeleteLocally(localCourseOfStudiesIdsWithTimeStamp: List<CourseOfStudiesIdWithTimeStamp>) =
        courseOfStudiesDao.findCourseOfStudiesToDeleteLocally(localCourseOfStudiesIdsWithTimeStamp)

    override suspend fun findCoursesOfStudiesNotUpToDateOfUser(localCourseOfStudiesIdsWithTimeStamp: List<CourseOfStudiesIdWithTimeStamp>) =
        courseOfStudiesDao.findCourseOfStudiesNotUpToDateOfUser(localCourseOfStudiesIdsWithTimeStamp)

    override suspend fun insertOrReplaceCourseOfStudies(courseOfStudies: MongoCourseOfStudies) =
        courseOfStudiesDao.insertOrReplaceCourseOfStudies(courseOfStudies, courseOfStudies.id, true)

    override suspend fun isCourseOfStudiesAbbreviationAlreadyUsed(courseOfStudies: MongoCourseOfStudies) =
        courseOfStudiesDao.isCourseOfStudiesAbbreviationAlreadyUsed(courseOfStudies)

    override suspend fun deleteCourseOfStudiesById(courseOfStudiesId: String) = deleteOneById<MongoCourseOfStudies>(courseOfStudiesId).also { wasAcknowledged ->
        if (wasAcknowledged) {
            questionnaireDao.removeCourseOfStudiesFromQuestionnaire(courseOfStudiesId)
        }
    }
}