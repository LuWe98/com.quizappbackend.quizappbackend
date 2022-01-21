package com.quizappbackend.model.mongodb.dao

import com.mongodb.client.model.ReplaceOneModel
import com.mongodb.client.model.ReplaceOptions
import com.mongodb.client.model.UpdateOptions
import com.quizappbackend.model.mongodb.documents.MongoFilledQuestionnaire
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.CoroutineCollection

class FilledQuestionnaireDaoImpl(
    override var collection: CoroutineCollection<MongoFilledQuestionnaire>
) : FilledQuestionnaireDao {

    override suspend fun findFilledQuestionnaire(userId: String, questionnaireId: String) = collection.findOne(
        and(MongoFilledQuestionnaire::userId eq userId, MongoFilledQuestionnaire::questionnaireId eq questionnaireId)
    )

    override suspend fun insertFilledQuestionnaireIfNotPresent(mongoFilledQuestionnaire: MongoFilledQuestionnaire) = collection.updateOne(
        and(
            MongoFilledQuestionnaire::userId eq mongoFilledQuestionnaire.userId,
            MongoFilledQuestionnaire::questionnaireId eq mongoFilledQuestionnaire.questionnaireId
        ),
        setValueOnInsert(mongoFilledQuestionnaire),
        UpdateOptions().upsert(true)
    ).wasAcknowledged()


    override suspend fun findFilledQuestionnairesForUser(userId: String, questionnairesToIgnore: List<String>) = collection.find(
        and(
            MongoFilledQuestionnaire::userId eq userId,
            MongoFilledQuestionnaire::questionnaireId nin questionnairesToIgnore
        )
    ).toList()


    override suspend fun deleteFilledQuestionnairesForUser(userId: String, questionnaireIds: List<String>) = collection.deleteMany(
        and(MongoFilledQuestionnaire::questionnaireId `in` questionnaireIds, MongoFilledQuestionnaire::userId eq userId)
    ).wasAcknowledged()


    override suspend fun replaceFilledQuestionnaireWith(mongoFilledQuestionnaire: MongoFilledQuestionnaire) = collection.replaceOne(
        and(
            MongoFilledQuestionnaire::userId eq mongoFilledQuestionnaire.userId,
            MongoFilledQuestionnaire::questionnaireId eq mongoFilledQuestionnaire.questionnaireId
        ),
        mongoFilledQuestionnaire,
        ReplaceOptions().upsert(true)
    ).wasAcknowledged()


    override suspend fun replaceFilledQuestionnairesWith(mongoFilledQuestionnaires: List<MongoFilledQuestionnaire>): Boolean {
        val bulkReplace = mongoFilledQuestionnaires.map {
            ReplaceOneModel(
                and(
                    MongoFilledQuestionnaire::userId eq it.userId,
                    MongoFilledQuestionnaire::questionnaireId eq it.questionnaireId
                ),
                it,
                ReplaceOptions().upsert(true)
            )
        }
        return collection.bulkWrite(bulkReplace).wasAcknowledged()
    }
}