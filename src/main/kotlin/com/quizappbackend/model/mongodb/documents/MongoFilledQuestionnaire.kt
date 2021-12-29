package com.quizappbackend.model.mongodb.documents

import kotlinx.serialization.Serializable
import org.bson.types.ObjectId

@Serializable
data class MongoFilledQuestionnaire(
    val questionnaireId: String = ObjectId().toHexString(),
    val userId: String = ObjectId().toHexString(),
    val questions: List<MongoFilledQuestion> = emptyList()
) : DocumentMarker


//TODO -> Idee ist, dass jeder Nutzer zu einem Fragebogen ganz viele Antworten Submitten kann, dass man Quasi eine Historie zu den gegebenen Antworten hat
// Man müsste dann die Fragen Lokal in einer eigenen Entität speichern, anstatt in den Antworten der Fragebögen mit isAnswerselected
// Man muss dann die gegebenen Fragen zu einem Fragebogen alle synchronisieren und abspeichern.
// Diese Haben dann auch eine eigene Id, sodass die Id wieder ein unique identifier zu den gegebenen Anwtorten ist.
// Wenn man dann einen Fragebogen auf dem Handy ausfüllt, werden die Antworten von diesem erst in der Entität/Dokument gespeichert, wenn man komplett durch ist
// Frage ist ob das sinnvoll ist, da der Fragebogen ja jederzeit abgebrochen werden kann. Wann sollen die Antworten dann wirklich gespeichert werden?
