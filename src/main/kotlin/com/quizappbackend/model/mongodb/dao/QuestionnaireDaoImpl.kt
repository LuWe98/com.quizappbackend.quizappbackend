package com.quizappbackend.model.mongodb.dao

import com.mongodb.client.model.UpdateManyModel
import com.quizappbackend.model.mongodb.documents.MongoQuestion
import com.quizappbackend.model.mongodb.documents.MongoQuestionnaire
import com.quizappbackend.model.mongodb.dto.MongoBrowsableQuestionnaire
import com.quizappbackend.model.mongodb.dto.RemoteQuestionnaireOrderBy
import com.quizappbackend.model.mongodb.properties.AuthorInfo
import com.quizappbackend.model.mongodb.properties.QuestionnaireVisibility
import io.ktor.util.date.*
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.CoroutineCollection

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

    //TODO -> Mit Bucket statt Skip Limit?
    override suspend fun getQuestionnairesPaged(
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
    ): List<MongoBrowsableQuestionnaire> {

        val bsonFilters = mutableListOf(
            MongoQuestionnaire::visibility eq QuestionnaireVisibility.PUBLIC,
            MongoQuestionnaire::title regex searchQuery,
            MongoQuestionnaire::id nin questionnaireIdsToIgnore,
            MongoQuestionnaire::authorInfo / AuthorInfo::userId ne userId
        )

        if (facultyIds.isNotEmpty()) {
            bsonFilters.add(MongoQuestionnaire::facultyIds `in` facultyIds)
        }

        if (courseOfStudiesIds.isNotEmpty()) {
            bsonFilters.add(MongoQuestionnaire::courseOfStudiesIds `in` courseOfStudiesIds)
        }

        if (authorIds.isNotEmpty()) {
            bsonFilters.add(MongoQuestionnaire::authorInfo / AuthorInfo::userId `in` authorIds)
        }

        return collection
            .find(and(bsonFilters))
            .sort(orderBy(orderBy.orderByProperty, ascending = ascending))
            .skip(kotlin.math.max((page - 1) * limit, 0))
            .projection(fields(exclude(MongoQuestionnaire::questions / MongoQuestion::answers)))
            .limit(limit)
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
}