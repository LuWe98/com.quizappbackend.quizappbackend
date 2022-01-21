package com.quizappbackend.routing.services

import com.quizappbackend.authentication.JwtAuth.userId
import com.quizappbackend.model.ktor.BackendRequest.*
import com.quizappbackend.model.ktor.BackendResponse.*
import com.quizappbackend.model.mongodb.MongoRepository
import com.quizappbackend.model.mongodb.documents.MongoFilledQuestionnaire
import com.quizappbackend.model.mongodb.documents.MongoQuestionnaire
import com.quizappbackend.utils.QuestionnaireCreatorUtil
import io.ktor.auth.jwt.*

class FilledQuestionnaireRouteServiceImpl(
    private val mongoRepository: MongoRepository
) : FilledQuestionnaireRouteService {

    override suspend fun handleInsertRequest(principle: JWTPrincipal, request: InsertFilledQuestionnaireRequest): InsertFilledQuestionnaireResponse {
        mongoRepository.findOneById<MongoQuestionnaire>(request.mongoFilledQuestionnaire.questionnaireId)?.let {
            mongoRepository.deleteManyById(request.mongoFilledQuestionnaire.questionnaireId, MongoFilledQuestionnaire::questionnaireId).let { wasAcknowledged ->
                return InsertFilledQuestionnaireResponse(
                    if (wasAcknowledged) InsertFilledQuestionnaireResponse.InsertFilledQuestionnaireResponseType.QUESTIONNAIRE_DOES_NOT_EXIST_ANYMORE
                    else InsertFilledQuestionnaireResponse.InsertFilledQuestionnaireResponseType.NOT_ACKNOWLEDGED
                )
            }
        }

        if (request.shouldBeIgnoredWhenAnotherIsPresent) {
            mongoRepository.insertFilledQuestionnaireIfNotPresent(principle.userId, request.mongoFilledQuestionnaire).let { acknowledged ->
                return InsertFilledQuestionnaireResponse(
                    if (acknowledged) InsertFilledQuestionnaireResponse.InsertFilledQuestionnaireResponseType.EMPTY_INSERTION_SKIPPED
                    else InsertFilledQuestionnaireResponse.InsertFilledQuestionnaireResponseType.NOT_ACKNOWLEDGED
                )

            }
        }

        mongoRepository.replaceFilledQuestionnaireWith(principle.userId, request.mongoFilledQuestionnaire).let { wasAcknowledged ->
            return InsertFilledQuestionnaireResponse(
                if (wasAcknowledged) InsertFilledQuestionnaireResponse.InsertFilledQuestionnaireResponseType.SUCCESSFUL
                else InsertFilledQuestionnaireResponse.InsertFilledQuestionnaireResponseType.NOT_ACKNOWLEDGED
            )
        }
    }


    override suspend fun handleInsertMultipleRequest(principle: JWTPrincipal, request: InsertFilledQuestionnairesRequest): InsertFilledQuestionnairesResponse {
        val mongoQuestionnaires = mongoRepository.findQuestionnairesWith(request.mongoFilledQuestionnaires)

        val nonExistentQuestionnaireIds = request.mongoFilledQuestionnaires
            .filter { filled -> mongoQuestionnaires.none { filled.questionnaireId == it.id } }
            .map(MongoFilledQuestionnaire::questionnaireId)

        val deleteAcknowledged = if (nonExistentQuestionnaireIds.isNotEmpty()) {
            mongoRepository.deleteManyByIds(nonExistentQuestionnaireIds, MongoFilledQuestionnaire::questionnaireId)
        } else true

        mongoRepository.replaceFilledQuestionnairesWith(principle.userId, request.mongoFilledQuestionnaires).let { wasAcknowledged ->
            return InsertFilledQuestionnairesResponse(
                notInsertedQuestionnaireIds = nonExistentQuestionnaireIds,
                if (wasAcknowledged) InsertFilledQuestionnairesResponse.InsertFilledQuestionnairesResponseType.SUCCESSFUL
                else InsertFilledQuestionnairesResponse.InsertFilledQuestionnairesResponseType.NOT_ACKNOWLEDGED
            )
        }
    }


    override suspend fun handleDeleteRequest(principle: JWTPrincipal, request: DeleteFilledQuestionnaireRequest): DeleteFilledQuestionnaireResponse {
        mongoRepository.deleteFilledQuestionnairesForUser(principle.userId, request.questionnaireIds).let { acknowledged ->
            return DeleteFilledQuestionnaireResponse(
                if (acknowledged) DeleteFilledQuestionnaireResponse.DeleteFilledQuestionnaireResponseType.SUCCESSFUL
                else DeleteFilledQuestionnaireResponse.DeleteFilledQuestionnaireResponseType.NOT_ACKNOWLEDGED
            )
        }
    }


    override suspend fun handleGenerateRandomRequest(principle: JWTPrincipal, amount: String): Boolean {
        val questionnaireList = QuestionnaireCreatorUtil.generateRandomFilledQuestionnaires(
            principle.userId,
            mongoRepository.getXAmountOfQuestionnaires(amount.toInt())
        )
        return mongoRepository.insertMany(questionnaireList.map { it.copy(userId = principle.userId) })
    }

}