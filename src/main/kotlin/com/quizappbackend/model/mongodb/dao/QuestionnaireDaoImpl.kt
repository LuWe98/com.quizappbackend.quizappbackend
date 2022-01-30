package com.quizappbackend.model.mongodb.dao

import com.mongodb.client.model.UpdateManyModel
import com.quizappbackend.model.ktor.BackendRequest
import com.quizappbackend.model.ktor.paging.BrowsableQuestionnairePageKeys
import com.quizappbackend.model.mongodb.documents.MongoQuestionnaire
import com.quizappbackend.model.mongodb.dto.MongoBrowsableQuestionnaire
import com.quizappbackend.model.mongodb.properties.AuthorInfo
import com.quizappbackend.model.mongodb.properties.QuestionnaireVisibility
import io.ktor.util.date.*
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.CoroutineFindPublisher

class QuestionnaireDaoImpl(
    override var collection: CoroutineCollection<MongoQuestionnaire>
) : QuestionnaireDao {

    override suspend fun updateQuestionnaireVisibility(questionnaireId: String, newVisibility: QuestionnaireVisibility) =
        collection.updateOne(
            MongoQuestionnaire::id eq questionnaireId,
            set(MongoQuestionnaire::visibility setTo newVisibility)
        ).wasAcknowledged()

    override suspend fun updateAuthorNameOfQuestionnaires(userId: String, newUserName: String) =
        collection.updateMany(
            MongoQuestionnaire::authorInfo / AuthorInfo::userId eq userId,
            setValue(MongoQuestionnaire::authorInfo / AuthorInfo::userName, newUserName)
        ).wasAcknowledged()

    override suspend fun getAllQuestionnairesConnectedToUser(
        userId: String,
        questionnairesToIgnore: List<String>,
        questionnairesToFind: List<String>
    ) = collection.find(
        and(
            or(
                MongoQuestionnaire::authorInfo / AuthorInfo::userId eq userId,
                MongoQuestionnaire::id `in` questionnairesToFind
            ),
            MongoQuestionnaire::id nin questionnairesToIgnore
        )
    ).toList()

    override suspend fun getQuestionnairesPaged(
        userId: String,
        request: BackendRequest.GetPagedQuestionnairesRequest
    ): List<MongoBrowsableQuestionnaire> {

        val bsonFilters = mutableListOf(
            MongoQuestionnaire::visibility eq QuestionnaireVisibility.PUBLIC,
            MongoQuestionnaire::title regex request.searchString,
            MongoQuestionnaire::id nin request.questionnaireIdsToIgnore,
            MongoQuestionnaire::authorInfo / AuthorInfo::userId ne userId
        )

        if (request.facultyIds.isNotEmpty()) {
            bsonFilters.add(MongoQuestionnaire::facultyIds `in` request.facultyIds)
        }

        if (request.courseOfStudiesIds.isNotEmpty()) {
            bsonFilters.add(MongoQuestionnaire::courseOfStudiesIds `in` request.courseOfStudiesIds)
        }

        if (request.authorIds.isNotEmpty()) {
            bsonFilters.add(MongoQuestionnaire::authorInfo / AuthorInfo::userId `in` request.authorIds)
        }

        return collection
            .find(and(bsonFilters))
            .sort(orderBy(request.orderBy.orderByProperty, ascending = request.ascending))
            .skip(kotlin.math.max((request.page - 1) * request.limit, 0))
            .projection(fields(exclude(MongoQuestionnaire::questions)))
            .limit(request.limit)
            .toList()
            .map(MongoQuestionnaire::asMongoQuestionnaireBrowse)
    }
    //.withDocumentClass<MongoBrowsableQuestionnaire>()
    //.map(MongoQuestionnaire::asMongoQuestionnaireBrowse)


    override suspend fun getXAmountOfQuestionnaires(limit: Int) = collection.find().limit(limit).toList()


    override suspend fun removeFacultyFromQuestionnaire(facultyId: String): Boolean {

        val updateTimestamp = UpdateManyModel<MongoQuestionnaire>(
            MongoQuestionnaire::facultyIds contains facultyId,
            set(MongoQuestionnaire::lastModifiedTimestamp setTo getTimeMillis())
        )

        val removeFacultyId = UpdateManyModel<MongoQuestionnaire>(
            MongoQuestionnaire::facultyIds contains facultyId,
            pull(MongoQuestionnaire::facultyIds, facultyId)
        )

        return collection.bulkWrite(updateTimestamp, removeFacultyId).wasAcknowledged()
    }


    override suspend fun removeCourseOfStudiesFromQuestionnaire(courseOfStudiesId: String): Boolean {
        val updateTimestamp = UpdateManyModel<MongoQuestionnaire>(
            MongoQuestionnaire::courseOfStudiesIds contains courseOfStudiesId,
            set(MongoQuestionnaire::lastModifiedTimestamp setTo getTimeMillis())
        )

        val removeFacultyId = UpdateManyModel<MongoQuestionnaire>(
            MongoQuestionnaire::courseOfStudiesIds contains courseOfStudiesId,
            pull(MongoQuestionnaire::courseOfStudiesIds, courseOfStudiesId)
        )

        return collection.bulkWrite(updateTimestamp, removeFacultyId).wasAcknowledged()
    }


    //    suspend fun insertSharedUserDataToList(questionnaireId: String, sharedWithUser: SharedWithInfo){
//        collection.updateOne(MongoQuestionnaire::id eq questionnaireId, addToSet(MongoQuestionnaire::sharedWithInfos, SharedWithInfo(userId, canEdit)))
//        collection.updateOne(MongoQuestionnaire::id eq questionnaireId, (MongoQuestionnaire::sharedWithInfos, sharedWithUser))
//    }

    //MongoQuestionnaire::sharedWithInfos elemMatch (SharedWithInfo::userId eq userId)




    override suspend fun getQuestionnairesPagedWithPageKeys(
        userId: String,
        request: BackendRequest.GetPagedQuestionnairesWithPageKeysRequest
    ) = getPage(true, userId, request)
        .projection(fields(exclude(MongoQuestionnaire::questions)))
        .toList()
        .map(MongoQuestionnaire::asMongoQuestionnaireBrowse)


    override suspend fun getPreviousPageKeys(
        userId: String,
        request: BackendRequest.GetPagedQuestionnairesWithPageKeysRequest
    ) = getPage(false, userId, request)
        .projection(fields(exclude(MongoQuestionnaire::questions, MongoQuestionnaire::courseOfStudiesIds, MongoQuestionnaire::facultyIds)))
        .toList()
        .let { questionnaires ->
            return@let when {
                questionnaires.isEmpty() -> null
                questionnaires.size != request.limit -> BrowsableQuestionnairePageKeys()
                else -> questionnaires.lastOrNull()?.let {
                    BrowsableQuestionnairePageKeys(it.id, it.title, it.lastModifiedTimestamp)
                }
            }
        }

    private fun getPage(
        isRefreshKeyFetch: Boolean,
        userId: String,
        request: BackendRequest.GetPagedQuestionnairesWithPageKeysRequest
    ): CoroutineFindPublisher<MongoQuestionnaire> {
        val bsonFilters = mutableListOf(
            MongoQuestionnaire::visibility eq QuestionnaireVisibility.PUBLIC,
            MongoQuestionnaire::title regex request.searchString,
            MongoQuestionnaire::id nin request.questionnaireIdsToIgnore,
            MongoQuestionnaire::authorInfo / AuthorInfo::userId ne userId,
        )

        if (request.facultyIds.isNotEmpty()) {
            bsonFilters.add(MongoQuestionnaire::facultyIds `in` request.facultyIds)
        }

        if (request.courseOfStudiesIds.isNotEmpty()) {
            bsonFilters.add(MongoQuestionnaire::courseOfStudiesIds `in` request.courseOfStudiesIds)
        }

        if (request.authorIds.isNotEmpty()) {
            bsonFilters.add(MongoQuestionnaire::authorInfo / AuthorInfo::userId `in` request.authorIds)
        }

        if (request.lastPageKeys.id.isNotEmpty()) {

            val isRegularFetchOrInvertedFetch = (isRefreshKeyFetch && request.ascending) || (!isRefreshKeyFetch && !request.ascending)

            val keyFilter = if(isRegularFetchOrInvertedFetch) {
                request.orderBy.orderByProperty gt request.orderBy.getValueOfQuestionnairePage(request.lastPageKeys)
            } else {
                request.orderBy.orderByProperty lt request.orderBy.getValueOfQuestionnairePage(request.lastPageKeys)
            }

            val equalFilter = request.orderBy.orderByProperty eq request.orderBy.getValueOfQuestionnairePage(request.lastPageKeys)

            val idFilter = if(isRegularFetchOrInvertedFetch) {
                MongoQuestionnaire::id gt request.lastPageKeys.id
            } else {
                MongoQuestionnaire::id lt request.lastPageKeys.id
            }

            bsonFilters.add(or(keyFilter, and(equalFilter, idFilter)))
        }

        return collection
            .find(and(bsonFilters))
            .sort(orderBy(request.orderBy.orderByProperty, MongoQuestionnaire::id, ascending = if (isRefreshKeyFetch) request.ascending else !request.ascending))
            .limit(request.limit)
    }
}