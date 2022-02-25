package com.quizappbackend.routing.services

import com.quizappbackend.authentication.JwtAuth.userId
import com.quizappbackend.extensions.findOneById
import com.quizappbackend.extensions.insertMany
import com.quizappbackend.model.ktor.BackendRequest.*
import com.quizappbackend.model.ktor.BackendResponse.*
import com.quizappbackend.model.mongodb.MongoRepository
import com.quizappbackend.model.mongodb.documents.MongoFilledQuestionnaire
import com.quizappbackend.model.mongodb.documents.MongoQuestionnaire
import com.quizappbackend.model.mongodb.dto.QuestionnaireIdWithTimestamp
import com.quizappbackend.utils.RandomQuestionnaireCreationUtil
import io.ktor.auth.jwt.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class QuestionnaireRouteServiceImpl(
    private val mongoRepository: MongoRepository
) : QuestionnaireRouteService {

    override suspend fun handleSyncQuestionnaireRequest(principle: JWTPrincipal, request: SyncQuestionnairesRequest): SyncQuestionnairesResponse = withContext(IO) {
        val filledQuestionnaires = mongoRepository.findFilledQuestionnairesForUser(
            userId = principle.userId,
            questionnairesToIgnore = request.unsyncedQuestionnaireIds + request.locallyDeletedQuestionnaireIds,
        )

        //ONLY DOWNLOAD FILLED QUESTIONNAIRES WHEN THERE IS NO QUESTIONNAIRE LOCALLY
        val filteredFilledQuestionnaires = async {
            filledQuestionnaires.filter { filledQuestionnaire ->
                request.syncedQuestionnaireIdsWithTimestamp.none { filledQuestionnaire.questionnaireId == it.id }
            }
        }

        val questionnaires = mongoRepository.getAllQuestionnairesConnectedToUser(
            userId = principle.userId,
            questionnairesToIgnore = request.unsyncedQuestionnaireIds + request.locallyDeletedQuestionnaireIds,
            questionnairesToFind = request.syncedQuestionnaireIdsWithTimestamp.map(QuestionnaireIdWithTimestamp::id)
                    + filledQuestionnaires.map(MongoFilledQuestionnaire::questionnaireId)
                    - request.locallyDeletedQuestionnaireIds.toSet()
        )

        //CHECKS IF THERE ARE QUESTIONNAIRES LOCALLY WHICH ARE NOT PRESENT IN THE DB ANYMORE
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


    override suspend fun handleInsertRequest(request: InsertQuestionnairesRequest): InsertQuestionnairesResponse {
        mongoRepository.insertOrReplaceQuestionnaires(request.mongoQuestionnaires).let { wasAcknowledged ->
            return InsertQuestionnairesResponse(
                if (wasAcknowledged) InsertQuestionnairesResponse.InsertQuestionnairesResponseType.SUCCESSFUL
                else InsertQuestionnairesResponse.InsertQuestionnairesResponseType.NOT_ACKNOWLEDGED
            )
        }
    }


    override suspend fun handleDeleteRequest(request: DeleteQuestionnaireRequest): DeleteQuestionnaireResponse {
        mongoRepository.deleteQuestionnaires(request.questionnaireIds).let { acknowledged ->
            return DeleteQuestionnaireResponse(
                if (acknowledged) DeleteQuestionnaireResponse.DeleteQuestionnaireResponseType.SUCCESSFUL
                else DeleteQuestionnaireResponse.DeleteQuestionnaireResponseType.NOT_ACKNOWLEDGED
            )
        }
    }


    override suspend fun handleGetPagedQuestionnairesRequest(principle: JWTPrincipal, request: GetPagedQuestionnairesRequest) = withContext(IO) {
        val asyncPage = async {
            mongoRepository.getQuestionnairesPaged(
                userId = principle.userId,
                request = request
            )
        }
        val asyncLastKey = async {
            mongoRepository.getPreviousPageKeys(
                userId = principle.userId,
                request = request
            )
        }
        return@withContext GetPagedQuestionnairesWithPageKeysResponse(
            previousKeys = asyncLastKey.await(),
            questionnaires = asyncPage.await()
        )
    }

    override suspend fun handleDownloadQuestionnaireRequest(principle: JWTPrincipal, request: GetQuestionnaireRequest): GetQuestionnaireResponse {
        mongoRepository.insertFilledQuestionnaireIfNotPresent(principle.userId, MongoFilledQuestionnaire(request.questionnaireId))

        mongoRepository.findOneById<MongoQuestionnaire>(request.questionnaireId)?.let {
            return GetQuestionnaireResponse(GetQuestionnaireResponse.GetQuestionnaireResponseType.SUCCESSFUL, it)
        }

        return GetQuestionnaireResponse(GetQuestionnaireResponse.GetQuestionnaireResponseType.QUESTIONNAIRE_NOT_FOUND)
    }


    override suspend fun handleChangeVisibilityRequest(request: ChangeQuestionnaireVisibilityRequest): ChangeQuestionnaireVisibilityResponse {
        mongoRepository.changeQuestionnaireVisibility(request.questionnaireId, request.newVisibility).let { wasAcknowledged ->
            return ChangeQuestionnaireVisibilityResponse(
                if (wasAcknowledged) ChangeQuestionnaireVisibilityResponse.ChangeQuestionnaireVisibilityResponseType.SUCCESSFUL
                else ChangeQuestionnaireVisibilityResponse.ChangeQuestionnaireVisibilityResponseType.NOT_ACKNOWLEDGED
            )
        }
    }


    override suspend fun handleShareRequest(request: ShareQuestionnaireWithUserRequest): ShareQuestionnaireWithUserResponse {
        mongoRepository.findUserByName(request.userName)?.let { user ->
            mongoRepository.findOneById<MongoQuestionnaire>(request.questionnaireId)?.let {
                if (it.authorInfo.userId == user.id) {
                    return ShareQuestionnaireWithUserResponse(ShareQuestionnaireWithUserResponse.ShareQuestionnaireWithUserResponseType.USER_IS_OWNER_OF_QUESTIONNAIRE)
                }
            } ?: run {
                return ShareQuestionnaireWithUserResponse(ShareQuestionnaireWithUserResponse.ShareQuestionnaireWithUserResponseType.QUESTIONNAIRE_DOES_NOT_EXIST)
            }

            mongoRepository.findFilledQuestionnaire(user.id, request.questionnaireId)?.let {
                return ShareQuestionnaireWithUserResponse(ShareQuestionnaireWithUserResponse.ShareQuestionnaireWithUserResponseType.ALREADY_SHARED_WITH_USER)
            }

            mongoRepository.insertFilledQuestionnaireIfNotPresent(user.id, MongoFilledQuestionnaire(request.questionnaireId)).let { acknowledged ->
                return ShareQuestionnaireWithUserResponse(
                    if (acknowledged) ShareQuestionnaireWithUserResponse.ShareQuestionnaireWithUserResponseType.SUCCESSFUL
                    else ShareQuestionnaireWithUserResponse.ShareQuestionnaireWithUserResponseType.NOT_ACKNOWLEDGED
                )
            }
        }

        return ShareQuestionnaireWithUserResponse(ShareQuestionnaireWithUserResponse.ShareQuestionnaireWithUserResponseType.USER_DOES_NOT_EXIST)
    }


    override suspend fun handleGenerateRandomQuestionnairesRequest(principle: JWTPrincipal, amount: String) = mongoRepository.insertMany(
        RandomQuestionnaireCreationUtil.generateRandomData(
            mongoRepository,
            principle,
            amount.toInt(),
            minQuestionsPerQuestionnaire = 10,
            maxQuestionsPerQuestionnaire = 50,
            minAnswersPerQuestion = 2,
            maxAnswersPerQuestion = 5
        )
    )
}