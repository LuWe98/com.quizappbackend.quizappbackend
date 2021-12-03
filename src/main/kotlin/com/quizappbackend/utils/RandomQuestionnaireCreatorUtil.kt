package com.quizappbackend.utils

import com.quizappbackend.authentication.JwtAuth.userId
import com.quizappbackend.authentication.JwtAuth.userName
import com.quizappbackend.model.databases.mongodb.documents.faculty.MongoCourseOfStudies
import com.quizappbackend.model.databases.mongodb.documents.faculty.MongoFaculty
import com.quizappbackend.model.databases.mongodb.documents.questionnaire.MongoAnswer
import com.quizappbackend.model.databases.mongodb.documents.questionnaire.MongoQuestion
import com.quizappbackend.model.databases.mongodb.documents.questionnaire.MongoQuestionnaire
import com.quizappbackend.model.databases.mongodb.documents.user.AuthorInfo
import com.quizappbackend.mongoRepository
import io.ktor.auth.jwt.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import kotlin.random.Random

object RandomQuestionnaireCreatorUtil {

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
                        !isMultipleChoice && atLeastOneAnswerCorrect -> false
                        else -> Random.nextBoolean()
                    }
                }

            if(!atLeastOneAnswerCorrect) {
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
    fun generateReadableQuestionnaires(principle: JWTPrincipal): List<MongoQuestionnaire> {
        val questionnaireOne = generateReadableQuestionnaireOne(principle)

        return listOf(questionnaireOne)
    }

    private fun generateReadableQuestionnaireOne(principle: JWTPrincipal): MongoQuestionnaire {
        val answersQuestionOne = listOf(
            MongoAnswer(answerText = "4", answerPosition = 0, isAnswerCorrect = false),
            MongoAnswer(answerText = "5", answerPosition = 1, isAnswerCorrect = false),
            MongoAnswer(answerText = "7", answerPosition = 2, isAnswerCorrect = false),
            MongoAnswer(answerText = "9", answerPosition = 3, isAnswerCorrect = true)
        )

        val questionOne = MongoQuestion(
            questionText = "Wie viele Fakultäten gibt es an der HFU?",
            questionPosition = 0,
            isMultipleChoice = false,
            answers = answersQuestionOne
        )


        val answersQuestionTwo = listOf(
            MongoAnswer(answerText = "45", answerPosition = 0, isAnswerCorrect = false),
            MongoAnswer(answerText = "55", answerPosition = 1, isAnswerCorrect = false),
            MongoAnswer(answerText = "65", answerPosition = 2, isAnswerCorrect = true),
            MongoAnswer(answerText = "75", answerPosition = 3, isAnswerCorrect = true)
        )

        val questionTwo = MongoQuestion(
            questionText = "Wie viele Studiengänge gibt es an der HFU?",
            questionPosition = 1,
            isMultipleChoice = false,
            answers = answersQuestionTwo
        )


        val answersQuestionThree = listOf(
            MongoAnswer(answerText = "Oder", answerPosition = 0, isAnswerCorrect = false),
            MongoAnswer(answerText = "Donau", answerPosition = 1, isAnswerCorrect = true),
            MongoAnswer(answerText = "Rhein", answerPosition = 2, isAnswerCorrect = false)
        )

        val questionThree = MongoQuestion(
            questionText = "Welcher dieser Flüsse ist der längste Fluss Deutschlands?",
            questionPosition = 2,
            isMultipleChoice = false,
            answers = answersQuestionThree
        )


        val answersQuestionFour = listOf(
            MongoAnswer(answerText = "Rohstoffe", answerPosition = 0, isAnswerCorrect = true),
            MongoAnswer(answerText = "Stromkosten der Produktion", answerPosition = 1, isAnswerCorrect = false),
            MongoAnswer(answerText = "Lieferkosten", answerPosition = 2, isAnswerCorrect = true),
            MongoAnswer(answerText = "Miete", answerPosition = 3, isAnswerCorrect = false)
        )

        val questionFour = MongoQuestion(
            questionText = "Was zählt zu der variablen Kostenart?",
            questionPosition = 3,
            isMultipleChoice = true,
            answers = answersQuestionFour
        )


        val answersQuestionFive = listOf(
            MongoAnswer(answerText = "B-Rabbit", answerPosition = 0, isAnswerCorrect = true),
            MongoAnswer(answerText = "Bizarre", answerPosition = 1, isAnswerCorrect = false),
            MongoAnswer(answerText = "Shady", answerPosition = 2, isAnswerCorrect = true),
            MongoAnswer(answerText = "Proof", answerPosition = 3, isAnswerCorrect = false)
        )

        val questionFive = MongoQuestion(
            questionText = "Unter welchem dieser Namen ist der Rapper Eminem noch bekannt?",
            questionPosition = 4,
            isMultipleChoice = true,
            answers = answersQuestionFive
        )


        val answersQuestionSix = listOf(
            MongoAnswer(answerText = "1954", answerPosition = 0, isAnswerCorrect = true),
            MongoAnswer(answerText = "1974", answerPosition = 1, isAnswerCorrect = true),
            MongoAnswer(answerText = "1978", answerPosition = 2, isAnswerCorrect = false),
            MongoAnswer(answerText = "1986", answerPosition = 3, isAnswerCorrect = false),
            MongoAnswer(answerText = "1990", answerPosition = 4, isAnswerCorrect = true)
        )

        val questionSix = MongoQuestion(
            questionText = "In welchen Jahren gewann Deutschland die WM im Fußball?",
            questionPosition = 5,
            isMultipleChoice = true,
            answers = answersQuestionSix
        )


        val answersQuestionSeven = listOf(
            MongoAnswer(answerText = "onCreate", answerPosition = 0, isAnswerCorrect = true),
            MongoAnswer(answerText = "onWait", answerPosition = 1, isAnswerCorrect = false),
            MongoAnswer(answerText = "onInitialized", answerPosition = 2, isAnswerCorrect = false),
            MongoAnswer(answerText = "onResumed", answerPosition = 3, isAnswerCorrect = true),
            MongoAnswer(answerText = "onRecreated", answerPosition = 4, isAnswerCorrect = false)
        )

        val questionSeven = MongoQuestion(
            questionText = "Welche dieser Methoden ist eine Methode des Activity Lebenszyklus?",
            questionPosition = 6,
            isMultipleChoice = true,
            answers = answersQuestionSeven
        )


        return MongoQuestionnaire(
            title = "Beispiel Fragebogen",
            courseOfStudiesIds = listOf("6192411146a8092ef8dcf092"),
            authorInfo = AuthorInfo(
                userId = principle.userId,
                userName = principle.userName
            ),
            subject = "Fach 123",
            questions = listOf(questionOne, questionTwo, questionThree, questionFour, questionFive, questionSix, questionSeven)
        )
    }
}