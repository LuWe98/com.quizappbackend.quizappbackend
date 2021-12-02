package com.quizappbackend.model.databases.mongodb.documents.questionnairefilled

import com.quizappbackend.model.databases.mongodb.documents.DocumentMarker
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId

@Serializable
data class MongoFilledQuestionnaire(
    var questionnaireId: String = ObjectId().toHexString(),
    var userId: String = ObjectId().toHexString(),
    var questions: List<MongoFilledQuestion> = emptyList()
) : DocumentMarker


//TODO -> Idee ist, dass jeder Nutzer zu einem Fragebogen ganz viele Antworten Submitten kann, dass man Quasi eine Historie zu den gegebenen Antworten hat
// Man müsste dann die Fragen Lokal in einer eigenen Entität speichern, anstatt in den Antworten der Fragebögen mit isAnswerselected
// Man muss dann die gegebenen Fragen zu einem Fragebogen alle synchronisieren und abspeichern.
// Diese Haben dann auch eine eigene Id, sodass die Id wieder ein unique identifier zu den gegebenen Anwtorten ist.
// Wenn man dann einen Fragebogen auf dem Handy ausfüllt, werden die Antworten von diesem erst in der Entität/Dokument gespeichert, wenn man komplett durch ist
// Frage ist ob das sinnvoll ist, da der Fragebogen ja jederzeit abgebrochen werden kann. Wann sollen die Antworten dann wirklich gespeichert werden?





//TODO -> Wurde entfernt, da Logik im Frontend ist, ob die Antworten drinbleiben dürfen oder nicht.
// Anworten werden eh bei dem nächsten update des users hochgeladen und überschrieben
//fun removeNonExistentQuestions(questionIdsToIgnore: List<String>, mongoQuestionnaire: MongoQuestionnaire): MongoFilledQuestionnaire {
//    return apply {
//        questions = questions.filter { filledQuestion ->
//            if (questionIdsToIgnore.contains(filledQuestion.questionId)) return@filter false
//
//            mongoQuestionnaire.questions.firstOrNull { it.id == filledQuestion.questionId }?.let { mongoQuestion ->
//                filledQuestion.selectedAnswerIds =
//                    if (!mongoQuestion.isMultipleChoice && filledQuestion.selectedAnswerIds.size > 1) {
//
//                        //TODO -> SCHAUEN OB DAS HIER GEHT MIT DEM FILTERN
//
//                        emptyList()
//                    } else filledQuestion.selectedAnswerIds.filter { answerId ->
//                        mongoQuestion.answers.any { it.id == answerId }
//                    }
//                return@filter true
//            } ?: return@filter false
//        }
//    }
//}
//QUESTIONNAIRE ALREADY EXISTS AND THEREFORE ALL GIVEN ANSWERS TO A QUESTIONNAIRE MUST BE UPDATED
//TODO -> Position der Frage sollte keine Auswirkungen haben !
//suspend fun replaceQuestionnaire(
//    currentQuestionnaire: MongoQuestionnaire,
//    newMongoQuestionnaire: MongoQuestionnaire
//): Boolean {
//
//    val questionsToIgnore = currentQuestionnaire.questions.filter { it !in newMongoQuestionnaire.questions }.map(MongoQuestion::id)
//
//    val filledQuestionnairesFor = findManyById(currentQuestionnaire.id, MongoFilledQuestionnaire::questionnaireId)
//
//    val updatedFilledQuestionnairesFor = filledQuestionnairesFor.map {
//        it.removeNonExistentQuestions(questionsToIgnore, newMongoQuestionnaire)
//    }
//
//    if (filledQuestionnairesFor != updatedFilledQuestionnairesFor) {
//        filledQuestionnaireDao.replaceFilledQuestionnaires(updatedFilledQuestionnairesFor)
//    }
//
//    //TODO -> IMMER SUCCESSFUL NEHMEN UND DANN NUR VERSCHIEDENE ERROR CASES
//    //TODO -> Anschauen, Statte replaced lieber inserted nehmen/Successfull
//    return replaceOneById(newMongoQuestionnaire, newMongoQuestionnaire.id)
//}
//TODO -> Das ist der Teil von der Questionnaire Route
//mongoRepository.insertOrReplaceQuestionnaire(request.mongoQuestionnaire).let {
//    wasAcknowledged ->
//    call.respond(
//        HttpStatusCode.OK, InsertQuestionnaireResponse(
//            if (wasAcknowledged) InsertQuestionnaireResponseType.SUCCESSFUL
//            else InsertQuestionnaireResponseType.NOT_ACKNOWLEDGED
//        )
//    )
//}
//mongoRepository.findOneById<MongoQuestionnaire>(request.mongoQuestionnaire.id)?.let {
//    currentQuestionnaire ->
//    mongoRepository.replaceQuestionnaire(currentQuestionnaire, request.mongoQuestionnaire).let { wasAcknowledged ->
//        call.respond(
//            HttpStatusCode.OK, InsertQuestionnaireResponse(
//                if (wasAcknowledged) InsertQuestionnaireResponseType.SUCCESSFUL
//                else InsertQuestionnaireResponseType.NOT_ACKNOWLEDGED
//            )
//        )
//    }
//    return@post
//}
//
//mongoRepository.insertOne(request.mongoQuestionnaire).let {
//    wasAcknowledged ->
//    call.respond(
//        HttpStatusCode.OK, InsertQuestionnaireResponse(
//            if (wasAcknowledged) InsertQuestionnaireResponseType.SUCCESSFUL
//            else InsertQuestionnaireResponseType.NOT_ACKNOWLEDGED
//        )
//    )
//}