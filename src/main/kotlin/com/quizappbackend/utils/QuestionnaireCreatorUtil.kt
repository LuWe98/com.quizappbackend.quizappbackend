package com.quizappbackend.utils

import com.quizappbackend.authentication.JwtAuth.userId
import com.quizappbackend.authentication.JwtAuth.userName
import com.quizappbackend.model.databases.mongodb.documents.MongoCourseOfStudies
import com.quizappbackend.model.databases.mongodb.documents.MongoFaculty
import com.quizappbackend.model.databases.mongodb.documents.MongoAnswer
import com.quizappbackend.model.databases.mongodb.documents.MongoQuestion
import com.quizappbackend.model.databases.mongodb.documents.MongoQuestionnaire
import com.quizappbackend.model.databases.mongodb.documents.user.AuthorInfo
import com.quizappbackend.mongoRepository
import io.ktor.auth.jwt.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import kotlin.random.Random

object QuestionnaireCreatorUtil {

    suspend fun generateRandomData(
        principle: JWTPrincipal,
        questionnaireAmount: Int,
        minQuestionsPerQuestionnaire: Int,
        maxQuestionsPerQuestionnaire: Int,
        minAnswersPerQuestion: Int,
        maxAnswersPerQuestion: Int
    ): List<MongoQuestionnaire> = withContext(IO) {
        if (maxQuestionsPerQuestionnaire < minQuestionsPerQuestionnaire)
            throw IllegalArgumentException("'maxQuestionsPerQuestionnaire' has to be bigger than 'minQuestionsPerQuestionnaire'")

        if (maxAnswersPerQuestion < minAnswersPerQuestion)
            throw IllegalArgumentException("'maxAnswersPerQuestion' has to be bigger than 'minAnswersPerQuestion'")

        val faculties = async { mongoRepository.getAllEntries<MongoFaculty>() }
        val coursesOfStudies = async { mongoRepository.getAllEntries<MongoCourseOfStudies>() }

        return@withContext List(questionnaireAmount) { index ->
            val randomFaculty = faculties.await().random()
            val randomCourseOfStudies = coursesOfStudies.await().filter { cos -> randomFaculty.id in cos.facultyIds }.random()

            MongoQuestionnaire(
                title = "Questionnaire Title No. $index",
                authorInfo = AuthorInfo(principle.userId, principle.userName),
                facultyIds = listOf(randomFaculty.id),
                courseOfStudiesIds = listOf(randomCourseOfStudies.id),
                subject = "IW",
                questions = generateQuestions(
                    minQuestionsPerQuestionnaire,
                    maxQuestionsPerQuestionnaire,
                    minAnswersPerQuestion,
                    maxAnswersPerQuestion
                )
            )
        }
    }

    private fun generateQuestions(
        min: Int,
        max: Int,
        minAnswersPerQuestion: Int,
        maxAnswersPerQuestion: Int
    ) = List(Random.nextInt(max - min + 1) + min) { index ->
        val isMultipleChoice = Random.nextBoolean()
        MongoQuestion(
            questionText = "Question Text No. $index",
            questionPosition = index,
            isMultipleChoice = isMultipleChoice,
            answers = generateAnswers(minAnswersPerQuestion, maxAnswersPerQuestion, isMultipleChoice)
        )
    }

    private fun generateAnswers(
        min: Int,
        max: Int,
        isMultipleChoice: Boolean
    ): List<MongoAnswer> {
        var atLeastOneAnswerCorrect = false
        val answerAmount = Random.nextInt(max - min + 1) + min

        return List(answerAmount) { index ->
            val isAnswerCorrect =
                if (index == answerAmount && !atLeastOneAnswerCorrect) {
                    true
                } else {
                    when {
                        isMultipleChoice -> Random.nextBoolean()
                        atLeastOneAnswerCorrect -> false
                        else -> Random.nextBoolean()
                    }
                }

            if (!atLeastOneAnswerCorrect) {
                atLeastOneAnswerCorrect = isAnswerCorrect
            }

            MongoAnswer(
                answerText = "Answer Text No. $index",
                answerPosition = index,
                isAnswerCorrect = isAnswerCorrect
            )
        }
    }


    //TODO -> Hier gescheite Fragebögen erstellen fürs testen
    suspend fun generateReadableQuestionnaires(principle: JWTPrincipal) = listOf(
        generateInformaticsQuestionnaire(principle),
        generateEconomicsQuestionnaire(principle)
    )

    private fun generateReadableQuestionnaireOne(principle: JWTPrincipal): MongoQuestionnaire {

        return MongoQuestionnaire(
            title = "Beispiel Fragebogen",
            courseOfStudiesIds = listOf("6192411146a8092ef8dcf092"),
            authorInfo = AuthorInfo(principle.userId, principle.userName),
            subject = "Beispiel Fach",
            questions = listOf(
                MongoQuestion(
                    questionText = "Wie viele Fakultäten gibt es an der HFU?",
                    questionPosition = 0,
                    isMultipleChoice = false,
                    answers = listOf(
                        MongoAnswer(answerText = "4", answerPosition = 0, isAnswerCorrect = false),
                        MongoAnswer(answerText = "5", answerPosition = 1, isAnswerCorrect = false),
                        MongoAnswer(answerText = "7", answerPosition = 2, isAnswerCorrect = false),
                        MongoAnswer(answerText = "9", answerPosition = 3, isAnswerCorrect = true)
                    )
                ),
                MongoQuestion(
                    questionText = "Wie viele Studiengänge gibt es an der HFU?",
                    questionPosition = 1,
                    isMultipleChoice = false,
                    answers = listOf(
                        MongoAnswer(answerText = "45", answerPosition = 0, isAnswerCorrect = false),
                        MongoAnswer(answerText = "55", answerPosition = 1, isAnswerCorrect = false),
                        MongoAnswer(answerText = "65", answerPosition = 2, isAnswerCorrect = true),
                        MongoAnswer(answerText = "75", answerPosition = 3, isAnswerCorrect = false)
                    )
                ),
                MongoQuestion(
                    questionText = "Welcher dieser Flüsse ist der längste Fluss Deutschlands?",
                    questionPosition = 2,
                    isMultipleChoice = false,
                    answers = listOf(
                        MongoAnswer(answerText = "Oder", answerPosition = 0, isAnswerCorrect = false),
                        MongoAnswer(answerText = "Donau", answerPosition = 1, isAnswerCorrect = true),
                        MongoAnswer(answerText = "Rhein", answerPosition = 2, isAnswerCorrect = false)
                    )
                ),
                MongoQuestion(
                    questionText = "Was zählt zu der variablen Kostenart?",
                    questionPosition = 3,
                    isMultipleChoice = true,
                    answers = listOf(
                        MongoAnswer(answerText = "Rohstoffe", answerPosition = 0, isAnswerCorrect = true),
                        MongoAnswer(answerText = "Stromkosten der Produktion", answerPosition = 1, isAnswerCorrect = false),
                        MongoAnswer(answerText = "Lieferkosten", answerPosition = 2, isAnswerCorrect = true),
                        MongoAnswer(answerText = "Miete", answerPosition = 3, isAnswerCorrect = false)
                    )
                ),
                MongoQuestion(
                    questionText = "Unter welchem dieser Namen ist der Rapper Eminem noch bekannt?",
                    questionPosition = 4,
                    isMultipleChoice = true,
                    answers = listOf(
                        MongoAnswer(answerText = "B-Rabbit", answerPosition = 0, isAnswerCorrect = true),
                        MongoAnswer(answerText = "Bizarre", answerPosition = 1, isAnswerCorrect = false),
                        MongoAnswer(answerText = "Shady", answerPosition = 2, isAnswerCorrect = true),
                        MongoAnswer(answerText = "Proof", answerPosition = 3, isAnswerCorrect = false)
                    )
                ),
                MongoQuestion(
                    questionText = "In welchen Jahren gewann Deutschland die WM im Fußball?",
                    questionPosition = 5,
                    isMultipleChoice = true,
                    answers = listOf(
                        MongoAnswer(answerText = "1954", answerPosition = 0, isAnswerCorrect = true),
                        MongoAnswer(answerText = "1974", answerPosition = 1, isAnswerCorrect = true),
                        MongoAnswer(answerText = "1978", answerPosition = 2, isAnswerCorrect = false),
                        MongoAnswer(answerText = "1986", answerPosition = 3, isAnswerCorrect = false),
                        MongoAnswer(answerText = "1990", answerPosition = 4, isAnswerCorrect = true)
                    )
                ),
                MongoQuestion(
                    questionText = "Welche dieser Methoden ist eine Methode des Activity Lebenszyklus?",
                    questionPosition = 6,
                    isMultipleChoice = true,
                    answers = listOf(
                        MongoAnswer(answerText = "onCreate", answerPosition = 0, isAnswerCorrect = true),
                        MongoAnswer(answerText = "onWait", answerPosition = 1, isAnswerCorrect = false),
                        MongoAnswer(answerText = "onInitialized", answerPosition = 2, isAnswerCorrect = false),
                        MongoAnswer(answerText = "onResumed", answerPosition = 3, isAnswerCorrect = true),
                        MongoAnswer(answerText = "onRecreated", answerPosition = 4, isAnswerCorrect = false)
                    )
                )
            )
        )
    }

    private suspend fun generateInformaticsQuestionnaire(principle: JWTPrincipal): MongoQuestionnaire {
        val faculty = mongoRepository.findFacultyWithAbbreviation("IN")!!

        return MongoQuestionnaire(
            title = "Informatics Questionnaire",
            authorInfo = AuthorInfo(principle.userId, principle.userName),
            subject = "Informatics Subject",
            questions = listOf(
                MongoQuestion(
                    questionText = "Von wie vielen Klassen kann eine Java Klasse erben?",
                    questionPosition = 0,
                    isMultipleChoice = false,
                    answers = listOf(
                        MongoAnswer(answerText = "1", answerPosition = 0, isAnswerCorrect = true),
                        MongoAnswer(answerText = "2", answerPosition = 1, isAnswerCorrect = false),
                        MongoAnswer(answerText = "3", answerPosition = 2, isAnswerCorrect = false),
                        MongoAnswer(answerText = "Unendlich", answerPosition = 3, isAnswerCorrect = false)
                    )
                ),
                MongoQuestion(
                    questionText = "Wie viele Bits hat ein Byte?",
                    questionPosition = 1,
                    isMultipleChoice = false,
                    answers = listOf(
                        MongoAnswer(answerText = "2", answerPosition = 0, isAnswerCorrect = false),
                        MongoAnswer(answerText = "4", answerPosition = 1, isAnswerCorrect = false),
                        MongoAnswer(answerText = "6", answerPosition = 2, isAnswerCorrect = false),
                        MongoAnswer(answerText = "8", answerPosition = 3, isAnswerCorrect = true)
                    )
                ),
                MongoQuestion(
                    questionText = "Wie viele Interfaces kann eine Java Klasse Implementieren?",
                    questionPosition = 2,
                    isMultipleChoice = false,
                    answers = listOf(
                        MongoAnswer(answerText = "Keine", answerPosition = 0, isAnswerCorrect = false),
                        MongoAnswer(answerText = "Unendlich", answerPosition = 1, isAnswerCorrect = true),
                        MongoAnswer(answerText = "2", answerPosition = 2, isAnswerCorrect = false),
                        MongoAnswer(answerText = "1", answerPosition = 3, isAnswerCorrect = false)
                    )
                ),
                MongoQuestion(
                    questionText = "Welche dieser Methoden ist keine Callback Methode des Activity Livecycle?",
                    questionPosition = 3,
                    isMultipleChoice = true,
                    answers = listOf(
                        MongoAnswer(answerText = "onPause", answerPosition = 0, isAnswerCorrect = false),
                        MongoAnswer(answerText = "onCreate", answerPosition = 1, isAnswerCorrect = false),
                        MongoAnswer(answerText = "onRecreate", answerPosition = 2, isAnswerCorrect = true),
                        MongoAnswer(answerText = "onInitiate", answerPosition = 3, isAnswerCorrect = true),
                        MongoAnswer(answerText = "onCreateView", answerPosition = 4, isAnswerCorrect = true)
                    )
                ),
                MongoQuestion(
                    questionText = "Können in einem Set die gleichen Werte mehrfach vorkommen?",
                    questionPosition = 4,
                    isMultipleChoice = false,
                    answers = listOf(
                        MongoAnswer(answerText = "Ja", answerPosition = 0, isAnswerCorrect = true),
                        MongoAnswer(answerText = "Nein", answerPosition = 1, isAnswerCorrect = false)
                    )
                ),
                MongoQuestion(
                    questionText = "Welche dieser Methoden ist eine Callback Methode des Fragment Livecycle?",
                    questionPosition = 5,
                    isMultipleChoice = true,
                    answers = listOf(
                        MongoAnswer(answerText = "onDestroy", answerPosition = 0, isAnswerCorrect = true),
                        MongoAnswer(answerText = "onCreate", answerPosition = 1, isAnswerCorrect = true),
                        MongoAnswer(answerText = "onStart", answerPosition = 2, isAnswerCorrect = true),
                        MongoAnswer(answerText = "onPause", answerPosition = 3, isAnswerCorrect = true),
                        MongoAnswer(answerText = "onStop", answerPosition = 4, isAnswerCorrect = true)
                    )
                ),
                MongoQuestion(
                    questionText = "Wie heißt die Operation mit welcher mehrere Tabellen einer SQL-Datenbank abgefragt werden können?",
                    questionPosition = 6,
                    isMultipleChoice = false,
                    answers = listOf(
                        MongoAnswer(answerText = "CONNECT", answerPosition = 0, isAnswerCorrect = false),
                        MongoAnswer(answerText = "JOIN", answerPosition = 1, isAnswerCorrect = true),
                        MongoAnswer(answerText = "UNION", answerPosition = 2, isAnswerCorrect = false)
                    )
                ),
                MongoQuestion(
                    questionText = "Wie viel Kilobyte entsprechen einem Megabyte?",
                    questionPosition = 7,
                    isMultipleChoice = false,
                    answers = listOf(
                        MongoAnswer(answerText = "10", answerPosition = 0, isAnswerCorrect = false),
                        MongoAnswer(answerText = "100", answerPosition = 1, isAnswerCorrect = false),
                        MongoAnswer(answerText = "1000", answerPosition = 2, isAnswerCorrect = true),
                        MongoAnswer(answerText = "10000", answerPosition = 3, isAnswerCorrect = false)
                    )
                ),
                MongoQuestion(
                    questionText = "Was ist die Basis des Oktalsystems?",
                    questionPosition = 8,
                    isMultipleChoice = false,
                    answers = listOf(
                        MongoAnswer(answerText = "2", answerPosition = 0, isAnswerCorrect = false),
                        MongoAnswer(answerText = "4", answerPosition = 1, isAnswerCorrect = false),
                        MongoAnswer(answerText = "6", answerPosition = 2, isAnswerCorrect = false),
                        MongoAnswer(answerText = "8", answerPosition = 3, isAnswerCorrect = true),
                        MongoAnswer(answerText = "12", answerPosition = 4, isAnswerCorrect = false),
                        MongoAnswer(answerText = "16", answerPosition = 5, isAnswerCorrect = false),
                        MongoAnswer(answerText = "20", answerPosition = 6, isAnswerCorrect = false)
                    )
                ),
                MongoQuestion(
                    questionText = "Für was steht die Abkürzung JWT?",
                    questionPosition = 9,
                    isMultipleChoice = false,
                    answers = listOf(
                        MongoAnswer(answerText = "Jason Web Token", answerPosition = 0, isAnswerCorrect = true),
                        MongoAnswer(answerText = "Jason Wire Transistor", answerPosition = 1, isAnswerCorrect = false),
                        MongoAnswer(answerText = "Jason Web Translation", answerPosition = 2, isAnswerCorrect = false),
                        MongoAnswer(answerText = "Jason Web Transaction", answerPosition = 3, isAnswerCorrect = false)
                    )
                )
            ),
            facultyIds = listOf(faculty.id),
            courseOfStudiesIds = listOf(mongoRepository.getCourseOfStudiesForFaculty(faculty.id).random().id)
        )
    }

    private suspend fun generateEconomicsQuestionnaire(principle: JWTPrincipal): MongoQuestionnaire {
        val faculty = mongoRepository.findFacultyWithAbbreviation("W")!!

        return MongoQuestionnaire(
            title = "Economics Questionnaire",
            authorInfo = AuthorInfo(principle.userId, principle.userName),
            subject = "Economics Subject",
            questions = listOf(
                MongoQuestion(
                    questionText = "Welche dieser Ziele gehört nicht zum magischen Viereck?",
                    questionPosition = 0,
                    isMultipleChoice = true,
                    answers = listOf(
                        MongoAnswer(answerText = "Preisniveaustabilität", answerPosition = 0, isAnswerCorrect = false),
                        MongoAnswer(answerText = "Innenwirtschaftliches Gleichgewicht", answerPosition = 1, isAnswerCorrect = true),
                        MongoAnswer(answerText = "Hoher Beschäftigungsgrad", answerPosition = 2, isAnswerCorrect = false),
                        MongoAnswer(answerText = "Stetig angemessenes Wirtschaftswachstum", answerPosition = 3, isAnswerCorrect = false)
                    )
                ),
                MongoQuestion(
                    questionText = "Welche Instanzen sind für die Umsetzung des Magischen Vierecks verantwortlich?",
                    questionPosition = 1,
                    isMultipleChoice = true,
                    answers = listOf(
                        MongoAnswer(answerText = "Staat", answerPosition = 0, isAnswerCorrect = true),
                        MongoAnswer(answerText = "Nicht Banken", answerPosition = 1, isAnswerCorrect = false),
                        MongoAnswer(answerText = "Notenbanken", answerPosition = 2, isAnswerCorrect = true)
                    )
                ),
                MongoQuestion(
                    questionText = "Wie lautet die Formel zur Berechnung des realen BIP?",
                    questionPosition = 2,
                    isMultipleChoice = false,
                    answers = listOf(
                        MongoAnswer(answerText = "BIP nominal / Preisindex", answerPosition = 0, isAnswerCorrect = true),
                        MongoAnswer(answerText = "Preisindex / BIP nominal", answerPosition = 1, isAnswerCorrect = false),
                        MongoAnswer(answerText = "BIP nominal * Preisindex", answerPosition = 2, isAnswerCorrect = false),
                        MongoAnswer(answerText = "BIP nominal * (Preisindex / 100)", answerPosition = 3, isAnswerCorrect = false),
                    )
                ),
                MongoQuestion(
                    questionText = "Was wird unter Inflation verstanden?",
                    questionPosition = 3,
                    isMultipleChoice = false,
                    answers = listOf(
                        MongoAnswer(answerText = "Der Anstieg des Preisniveaus", answerPosition = 0, isAnswerCorrect = true),
                        MongoAnswer(answerText = "Der Abfall des Preisniveaus", answerPosition = 1, isAnswerCorrect = false),
                        MongoAnswer(answerText = "Die Konsistenz des Preisniveaus", answerPosition = 2, isAnswerCorrect = false)
                    )
                ),
                MongoQuestion(
                    questionText = "Welche Marktformen gibt es?",
                    questionPosition = 4,
                    isMultipleChoice = false,
                    answers = listOf(
                        MongoAnswer(answerText = "Polypol und Monopol", answerPosition = 0, isAnswerCorrect = false),
                        MongoAnswer(answerText = "Polypol und Oligopol", answerPosition = 1, isAnswerCorrect = false),
                        MongoAnswer(answerText = "Oligopol und Monopol", answerPosition = 2, isAnswerCorrect = false),
                        MongoAnswer(answerText = "Polypol, Oligopol und Monopol", answerPosition = 3, isAnswerCorrect = true),
                        MongoAnswer(answerText = "Polypol, Trilopol und Monopol", answerPosition = 4, isAnswerCorrect = false)
                    )
                ),
                MongoQuestion(
                    questionText = "Nach welchen größen richtet sich hauptsächlich der Markt?",
                    questionPosition = 5,
                    isMultipleChoice = true,
                    answers = listOf(
                        MongoAnswer(answerText = "Angebot", answerPosition = 0, isAnswerCorrect = true),
                        MongoAnswer(answerText = "Nachfrage", answerPosition = 1, isAnswerCorrect = true),
                        MongoAnswer(answerText = "Bestand", answerPosition = 2, isAnswerCorrect = false),
                        MongoAnswer(answerText = "Nachfrage", answerPosition = 3, isAnswerCorrect = false),
                        MongoAnswer(answerText = "Absatz", answerPosition = 4, isAnswerCorrect = false)
                    )
                )
            ),
            facultyIds = listOf(faculty.id),
            courseOfStudiesIds = listOf(mongoRepository.getCourseOfStudiesForFaculty(faculty.id).random().id)
        )
    }
}