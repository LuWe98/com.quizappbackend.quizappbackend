package com.quizappbackend.model.databases.mongodb.dao

import com.mongodb.client.model.BucketOptions
import com.quizappbackend.model.databases.dto.RemoteQuestionnaireOrderBy
import com.quizappbackend.model.databases.mongodb.documents.MongoQuestion
import com.quizappbackend.model.databases.mongodb.documents.MongoQuestionnaire
import com.quizappbackend.model.databases.QuestionnaireVisibility
import com.quizappbackend.model.databases.dto.MongoBrowsableQuestionnaire
import com.quizappbackend.model.databases.mongodb.documents.user.AuthorInfo
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.CoroutineCollection

class QuestionnaireDao(
    override var collection: CoroutineCollection<MongoQuestionnaire>
) : BaseDao<MongoQuestionnaire>(collection) {

    suspend fun getXAmountOfQuestionnaires(limit: Int) = collection.find().limit(limit).toList()

    suspend fun changeQuestionnaireVisibility(questionnaireId: String, newVisibility: QuestionnaireVisibility) =
        collection.updateOne(
            MongoQuestionnaire::id eq questionnaireId,
            set(MongoQuestionnaire::questionnaireVisibility setTo newVisibility)
        ).wasAcknowledged()

    suspend fun updateAuthorNameInQuestionnaires(userId: String, newUserName: String) =
        collection.updateMany(
            MongoQuestionnaire::authorInfo / AuthorInfo::userId eq userId,
            setValue(MongoQuestionnaire::authorInfo / AuthorInfo::userName, newUserName)
        ).wasAcknowledged()

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
    ): List<MongoBrowsableQuestionnaire> {

        val bsonFilters = mutableListOf(
            MongoQuestionnaire::questionnaireVisibility eq QuestionnaireVisibility.PUBLIC,
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

    //TODO -> Mit Bucket statt Skip Limit?
    suspend fun getQuestionnairesPagedBucket(userId: String, limit: Int, page: Int, searchQuery: String): List<MongoQuestionnaire> {
        val group = group(MongoQuestionnaire::id)
        val bucket = bucket(group, mutableListOf(0, limit), BucketOptions())
        return collection.aggregate<MongoQuestionnaire>(listOf(bucket)).toList()
    }


//    suspend fun insertSharedUserDataToList(questionnaireId: String, sharedWithUser: SharedWithInfo){
//        collection.updateOne(MongoQuestionnaire::id eq questionnaireId, addToSet(MongoQuestionnaire::sharedWithInfos, SharedWithInfo(userId, canEdit)))
//        collection.updateOne(MongoQuestionnaire::id eq questionnaireId, (MongoQuestionnaire::sharedWithInfos, sharedWithUser))
//    }


    suspend fun getAllQuestionnairesConnectedToUser(
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

    //MongoQuestionnaire::sharedWithInfos elemMatch (SharedWithInfo::userId eq userId)

    /*
        suspend fun deleteAnswerWith(questionnaireID: String, answerID: String) = collection.updateOne(
        MongoQuestionnaire::id eq questionnaireID,
        pullByFilter(MongoQuestionnaire::questions.allPosOp / MongoQuestion::answers, MongoAnswer::id eq answerID)
    ).wasAcknowledged()
     */



}