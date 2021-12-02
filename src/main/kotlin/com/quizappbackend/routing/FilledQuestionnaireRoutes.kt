package com.quizappbackend.routing

import com.quizappbackend.authentication.JwtAuth.userId
import com.quizappbackend.authentication.JwtAuth.userPrinciple
import com.quizappbackend.model.networking.requests.DeleteFilledQuestionnaireRequest
import com.quizappbackend.model.networking.requests.InsertFilledQuestionnaireRequest
import com.quizappbackend.model.networking.requests.InsertFilledQuestionnairesRequest
import com.quizappbackend.model.networking.responses.DeleteFilledQuestionnaireResponse
import com.quizappbackend.model.networking.responses.DeleteFilledQuestionnaireResponse.DeleteFilledQuestionnaireResponseType
import com.quizappbackend.model.networking.responses.InsertFilledQuestionnaireResponse
import com.quizappbackend.model.networking.responses.InsertFilledQuestionnaireResponse.InsertFilledQuestionnaireResponseType
import com.quizappbackend.model.networking.responses.InsertFilledQuestionnairesResponse
import com.quizappbackend.model.networking.responses.InsertFilledQuestionnairesResponse.InsertFilledQuestionnairesResponseType
import com.quizappbackend.model.databases.mongodb.documents.questionnaire.MongoQuestionnaire
import com.quizappbackend.model.databases.mongodb.documents.questionnairefilled.MongoFilledQuestionnaire
import com.quizappbackend.mongoRepository
import com.quizappbackend.routing.ApiPaths.*
import com.quizappbackend.utils.RandomFilledQuestionnaireCreatorUtil
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Routing.registerFilledQuestionnaireRoutes() {
    registerInsertFilledQuestionnaireRoute()
    registerInsertFilledQuestionnairesRoute()
    registerDeleteFilledQuestionnaireRoute()
    registerCreateRandomFilledQuestionnairesRoute()
}


private fun Route.registerInsertFilledQuestionnaireRoute() = authenticate {
    post(FilledQuestionnairePaths.INSERT) {
        val request = call.receive<InsertFilledQuestionnaireRequest>()

        val questionnaire = mongoRepository.findOneById<MongoQuestionnaire>(request.mongoFilledQuestionnaire.questionnaireId)

        if (questionnaire == null) {
            mongoRepository.deleteManyById(request.mongoFilledQuestionnaire.questionnaireId, MongoFilledQuestionnaire::questionnaireId).let { wasAcknowledged ->
                call.respond(
                    HttpStatusCode.OK,
                    InsertFilledQuestionnaireResponse(
                        if(wasAcknowledged) InsertFilledQuestionnaireResponseType.QUESTIONNAIRE_DOES_NOT_EXIST_ANYMORE
                        else InsertFilledQuestionnaireResponseType.NOT_ACKNOWLEDGED
                    )
                )
            }
            return@post
        }

        if (request.shouldBeIgnoredWhenAnotherIsPresent) {
            mongoRepository.insertFilledQuestionnaireIfNotAlreadyPresent(request.mongoFilledQuestionnaire, userPrinciple.userId).let { result ->
                call.respond(
                    HttpStatusCode.OK,
                    InsertFilledQuestionnaireResponse(
                        if(result.wasAcknowledged()) InsertFilledQuestionnaireResponseType.EMPTY_INSERTION_SKIPPED
                        else InsertFilledQuestionnaireResponseType.NOT_ACKNOWLEDGED
                    )
                )
            }
            return@post
        }

        mongoRepository.replaceOneFilledQuestionnaireWith(request.mongoFilledQuestionnaire, userPrinciple.userId).let { wasAcknowledged ->
            call.respond(
                HttpStatusCode.OK,
                InsertFilledQuestionnaireResponse(
                    if(wasAcknowledged) InsertFilledQuestionnaireResponseType.SUCCESSFUL
                    else InsertFilledQuestionnaireResponseType.NOT_ACKNOWLEDGED
                )
            )
        }
    }
}


private fun Route.registerInsertFilledQuestionnairesRoute() = authenticate {
    post(FilledQuestionnairePaths.INSERTS) {
        val request = call.receive<InsertFilledQuestionnairesRequest>()

        val questionnaires = mongoRepository.findQuestionnairesWith(request.mongoFilledQuestionnaires)

        val nonExistentQuestionnaireIds = request.mongoFilledQuestionnaires
            .filter { filled -> questionnaires.none { filled.questionnaireId == it.id } }
            .map(MongoFilledQuestionnaire::questionnaireId)

        val deleteAcknowledged = if (nonExistentQuestionnaireIds.isNotEmpty()) {
            mongoRepository.deleteManyByIds(nonExistentQuestionnaireIds, MongoFilledQuestionnaire::questionnaireId)
        } else true

        //TODO -> HERE EINEN CHECK MACHEN, OB DER FRAGEBOGEN DEN AUFBAU DER ANTWORTEN NOCH HAT
        //TODO -> MIT DEM "questionnaire" von oben damit man nicht zweimal abfragen machen muss
        //TODO -> !! Aufbau des Fragebogens ist egal, da im Client eh die Angabe bereinigt wird, und es nicht zu falschen Zuständen kommen kann !!
        mongoRepository.replaceManyFilledQuestionnairesWith(request.mongoFilledQuestionnaires, userPrinciple.userId).let { wasAcknowledged ->
            call.respond(
                HttpStatusCode.OK,
                InsertFilledQuestionnairesResponse(
                    notInsertedQuestionnaireIds = nonExistentQuestionnaireIds,
                    if(wasAcknowledged) InsertFilledQuestionnairesResponseType.SUCCESSFUL
                    else InsertFilledQuestionnairesResponseType.NOT_ACKNOWLEDGED
                )
            )
        }
    }
}


private fun Route.registerDeleteFilledQuestionnaireRoute() = authenticate {
    delete(FilledQuestionnairePaths.DELETE) {
        val request = call.receive<DeleteFilledQuestionnaireRequest>()

        mongoRepository.deleteFilledQuestionnairesForUser(userPrinciple.userId, request.questionnaireIds).let { acknowledged ->
            call.respond(
                HttpStatusCode.OK,
                DeleteFilledQuestionnaireResponse(
                    if (acknowledged) DeleteFilledQuestionnaireResponseType.SUCCESSFUL
                    else DeleteFilledQuestionnaireResponseType.NOT_ACKNOWLEDGED
                )
            )
        }
    }
}


private fun Route.registerCreateRandomFilledQuestionnairesRoute() = authenticate {
    post(FilledQuestionnairePaths.GENERATE_RANDOM) {
        val request = call.receive<String>()

        val questionnaireList = RandomFilledQuestionnaireCreatorUtil.generateRandomData(
            userPrinciple.userId,
            mongoRepository.getXAmountOfQuestionnaires(request.toInt())
        )

        mongoRepository.insertMany(questionnaireList.onEach { it.userId = userPrinciple.userId }).let { acknowledged ->
            call.respond(HttpStatusCode.OK, acknowledged)
        }
    }
}