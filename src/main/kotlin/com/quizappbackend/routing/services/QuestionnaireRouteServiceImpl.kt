package com.quizappbackend.routing.services

import com.quizappbackend.authentication.JwtAuth.userId
import com.quizappbackend.model.ktor.BackendRequest.*
import com.quizappbackend.model.ktor.BackendResponse.*
import com.quizappbackend.model.mongodb.MongoRepository
import com.quizappbackend.model.mongodb.documents.MongoFilledQuestionnaire
import com.quizappbackend.model.mongodb.documents.MongoQuestionnaire
import com.quizappbackend.model.mongodb.dto.MongoBrowsableQuestionnaire
import com.quizappbackend.model.mongodb.dto.QuestionnaireIdWithTimestamp
import com.quizappbackend.utils.QuestionnaireCreatorUtil
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
        mongoRepository.insertOrReplaceQuestionnaire(request.mongoQuestionnaires).let { wasAcknowledged ->
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


    override suspend fun handleGetPagesQuestionnairesRequest(principle: JWTPrincipal, request: GetPagedQuestionnairesRequest): List<MongoBrowsableQuestionnaire> {
        return mongoRepository.getQuestionnairesPaged(
            userId = principle.userId,
            limit = request.limit,
            page = request.page,
            searchQuery = request.searchString,
            questionnaireIdsToIgnore = request.questionnaireIdsToIgnore,
            facultyIds = request.facultyIds,
            courseOfStudiesIds = request.courseOfStudiesIds,
            authorIds = request.authorIds,
            orderBy = request.remoteQuestionnaireOrderBy,
            ascending = request.ascending
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


    override suspend fun handleGenerateRandomQuestionnairesRequest(principle: JWTPrincipal, amount: String): Boolean {
        val questionnaireList = QuestionnaireCreatorUtil.generateRandomData(
            mongoRepository,
            principle,
            amount.toInt(),
            minQuestionsPerQuestionnaire = 10,
            maxQuestionsPerQuestionnaire = 50,
            minAnswersPerQuestion = 2,
            maxAnswersPerQuestion = 5
        )

        return mongoRepository.insertMany(questionnaireList)
    }



    //OLD SHARE
    //        mongoRepository.getUserWithUserName(request.userName)?.let { user ->
//            val sharedWithInfo = SharedWithInfo(user.id, request.canEdit)
//
//            mongoRepository.findOneById<MongoQuestionnaire>(request.questionnaireId)?.let { questionnaire ->
//                questionnaire.sharedWithInfos.firstOrNull{ it.userId == user.id}.let {
//                    if(it == null){
//                        val updated = questionnaire.copy(sharedWithInfos = questionnaire.sharedWithInfos.toMutableList().apply {
//                            add(sharedWithInfo)
//                        })
//                        mongoRepository.updateOneById(updated.id, updated).let { wasAcknowledged ->
//                            call.respond(HttpStatusCode.OK, ShareQuestionnaireWithUserResponse(ShareQuestionnaireWithUserResponseType.SUCCESSFUL))
//                        }
//                    } else {
//                        if(it == sharedWithInfo){
//                            call.respond(HttpStatusCode.OK, ShareQuestionnaireWithUserResponse(ShareQuestionnaireWithUserResponseType.ALREADY_SHARED_WITH_USER))
//                        } else {
//                            val updated = questionnaire.copy(sharedWithInfos = questionnaire.sharedWithInfos.toMutableList().apply {
//                                remove(it)
//                                add(sharedWithInfo)}
//                            )
//                            mongoRepository.updateOneById(updated.id, updated).let { wasAcknowledged ->
//                                call.respond(HttpStatusCode.OK, ShareQuestionnaireWithUserResponse(ShareQuestionnaireWithUserResponseType.SUCCESSFUL))
//                            }
//                        }
//                    }
//                }
//                return@post
//            }
//
//            call.respond(HttpStatusCode.OK, ShareQuestionnaireWithUserResponse(ShareQuestionnaireWithUserResponseType.QUESTIONNAIRE_DOES_NOT_EXIST))
//            return@post
//        }
//
//        call.respond(HttpStatusCode.OK, ShareQuestionnaireWithUserResponse(ShareQuestionnaireWithUserResponseType.USER_DOES_NOT_EXIST))
}