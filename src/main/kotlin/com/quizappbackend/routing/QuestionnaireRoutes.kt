package com.quizappbackend.routing

import com.quizappbackend.authentication.JwtAuth.userId
import com.quizappbackend.authentication.JwtAuth.userPrinciple
import com.quizappbackend.model.networking.requests.*
import com.quizappbackend.model.networking.responses.*
import com.quizappbackend.model.networking.responses.ChangeQuestionnaireVisibilityResponse.ChangeQuestionnaireVisibilityResponseType
import com.quizappbackend.model.networking.responses.DeleteQuestionnaireResponse.DeleteQuestionnaireResponseType
import com.quizappbackend.model.networking.responses.GetQuestionnaireResponse.GetQuestionnaireResponseType
import com.quizappbackend.model.networking.responses.InsertQuestionnairesResponse.InsertQuestionnairesResponseType
import com.quizappbackend.model.networking.responses.ShareQuestionnaireWithUserResponse.ShareQuestionnaireWithUserResponseType
import com.quizappbackend.model.databases.mongodb.documents.MongoQuestionnaire
import com.quizappbackend.model.databases.mongodb.documents.MongoFilledQuestionnaire
import com.quizappbackend.mongoRepository
import com.quizappbackend.routing.ApiPaths.*
import com.quizappbackend.utils.QuestionnaireCreatorUtil
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*


fun Routing.registerQuestionnaireRoutes() {
    registerInsertQuestionnairesRoute()
    registerDeleteQuestionnaireRoute()
    registerCreateRandomQuestionnairesRoute()
    registerChangeQuestionnaireVisibilityRoute()
    registerGetPagedQuestionnairesRoute()
    registerGetQuestionnairesForSyncronizationRoute()
    registerDownloadQuestionnaireRoute()
    registerShareQuestionnaireRoute()
    registerCreateReadableExampleQuestionnaires()

    registerFindUnconnectedQuestionnaires()
}


private fun Route.registerGetQuestionnairesForSyncronizationRoute() = authenticate {
    post(QuestionnairePaths.SYNC) {
        val request = call.receive<SyncQuestionnairesRequest>()

        mongoRepository.generateSyncQuestionnairesResponse(userPrinciple.userId, request).let { response ->
            call.respond(HttpStatusCode.OK, response)
        }
    }
}


private fun Route.registerInsertQuestionnairesRoute() = authenticate {
    post(QuestionnairePaths.INSERT) {
        val request = call.receive<InsertQuestionnairesRequest>()

        mongoRepository.insertOrReplaceQuestionnaire(request.mongoQuestionnaires).let { wasAcknowledged ->
            call.respond(
                HttpStatusCode.OK, InsertQuestionnairesResponse(
                    if (wasAcknowledged) InsertQuestionnairesResponseType.SUCCESSFUL
                    else InsertQuestionnairesResponseType.NOT_ACKNOWLEDGED
                )
            )
        }
    }
}


private fun Route.registerDeleteQuestionnaireRoute() = authenticate {
    delete(QuestionnairePaths.DELETE) {
        val request = call.receive<DeleteQuestionnaireRequest>()

        mongoRepository.deleteQuestionnaires(request.questionnaireIds).let { acknowledged ->
            call.respond(
                HttpStatusCode.OK,
                DeleteQuestionnaireResponse(
                    if (acknowledged) DeleteQuestionnaireResponseType.SUCCESSFUL
                    else DeleteQuestionnaireResponseType.NOT_ACKNOWLEDGED
                )
            )
        }
    }
}


private fun Route.registerGetPagedQuestionnairesRoute() = authenticate {
    post(QuestionnairePaths.PAGED) {
        val request = call.receive<GetPagedQuestionnairesRequest>()

        call.respond(
            HttpStatusCode.OK,
            mongoRepository.getQuestionnairesPaged(
                userId = userPrinciple.userId,
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
        )
    }
}


private fun Route.registerDownloadQuestionnaireRoute() = authenticate {
    post(QuestionnairePaths.DOWNLOAD) {
        val request = call.receive<GetQuestionnaireRequest>()

        mongoRepository.insertFilledQuestionnaireIfNotAlreadyPresent(MongoFilledQuestionnaire(request.questionnaireId), userPrinciple.userId)

        mongoRepository.findOneById<MongoQuestionnaire>(request.questionnaireId)?.let {
            call.respond(HttpStatusCode.OK, GetQuestionnaireResponse(GetQuestionnaireResponseType.SUCCESSFUL, it))
            return@post
        }

        call.respond(HttpStatusCode.OK, GetQuestionnaireResponse(GetQuestionnaireResponseType.QUESTIONNAIRE_NOT_FOUND))
    }
}


private fun Route.registerChangeQuestionnaireVisibilityRoute() = authenticate {
    post(QuestionnairePaths.UPDATE_VISIBILITY) {
        val request = call.receive<ChangeQuestionnaireVisibilityRequest>()

        mongoRepository.changeQuestionnaireVisibility(request.questionnaireId, request.newVisibility).let { wasAcknowledged ->
            call.respond(
                HttpStatusCode.OK, ChangeQuestionnaireVisibilityResponse(
                    if (wasAcknowledged) ChangeQuestionnaireVisibilityResponseType.SUCCESSFUL
                    else ChangeQuestionnaireVisibilityResponseType.NOT_ACKNOWLEDGED
                )
            )
        }
    }
}


//TODO -> STATTDESSEN ZU DER LISTE IM QUESTIONNAIRE HINZUFÜGEN STATT EMPTY FILLED QUESTIONNAIRE ZU ERSTELLEN!!!
private fun Route.registerShareQuestionnaireRoute() = authenticate {
    post(QuestionnairePaths.SHARE) {

        val request = call.receive<ShareQuestionnaireWithUserRequest>()

        mongoRepository.getUserWithUserName(request.userName)?.let { user ->
            mongoRepository.findOneById<MongoQuestionnaire>(request.questionnaireId)?.let {
                if (it.authorInfo.userId == user.id) {
                    call.respond(ShareQuestionnaireWithUserResponse(ShareQuestionnaireWithUserResponseType.USER_IS_OWNER_OF_QUESTIONNAIRE))
                    return@post
                }
            } ?: run {
                call.respond(ShareQuestionnaireWithUserResponse(ShareQuestionnaireWithUserResponseType.QUESTIONNAIRE_DOES_NOT_EXIST))
                return@post
            }

            mongoRepository.getFilledQuestionnaire(user.id, request.questionnaireId)?.let {
                call.respond(ShareQuestionnaireWithUserResponse(ShareQuestionnaireWithUserResponseType.ALREADY_SHARED_WITH_USER))
                return@post
            }

            mongoRepository.insertFilledQuestionnaireIfNotAlreadyPresent(MongoFilledQuestionnaire(request.questionnaireId), user.id).let { result ->
                call.respond(
                    ShareQuestionnaireWithUserResponse(
                        if(result.wasAcknowledged()) ShareQuestionnaireWithUserResponseType.SUCCESSFUL
                        else ShareQuestionnaireWithUserResponseType.NOT_ACKNOWLEDGED
                    )
                )
                return@post
            }
        }

        call.respond(ShareQuestionnaireWithUserResponse(ShareQuestionnaireWithUserResponseType.USER_DOES_NOT_EXIST))
    }
}

private fun Route.registerCreateRandomQuestionnairesRoute() = authenticate {
    post(QuestionnairePaths.GENERATE_RANDOM) {
        val request = call.receive<String>()

        val questionnaireList = QuestionnaireCreatorUtil.generateRandomData(
            userPrinciple,
            request.toInt(),
            minQuestionsPerQuestionnaire = 10,
            maxQuestionsPerQuestionnaire = 50,
            minAnswersPerQuestion = 2,
            maxAnswersPerQuestion = 5
        )

        call.respond(HttpStatusCode.OK, mongoRepository.insertMany(questionnaireList))
    }
}


private fun Route.registerCreateReadableExampleQuestionnaires() = authenticate {
    post("/questionnaire/readable") {
        call.respond(HttpStatusCode.OK, mongoRepository.insertMany(QuestionnaireCreatorUtil.generateReadableQuestionnaires(userPrinciple)))
    }
}


private fun Route.registerFindUnconnectedQuestionnaires() {
    get("/questionnaire/unconnected") {
        call.respond(mongoRepository.findUnconnectedQuestionnaires())
    }
}


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