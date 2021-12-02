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

        return@withContext generateQuestionnaires(questionnaireAmount, principle, faculties.await(), coursesOfStudies.await()).onEach { questionnaire ->
            questionnaire.questions = generateQuestions(minQuestionsPerQuestionnaire, maxQuestionsPerQuestionnaire).onEach { question ->
                question.answers = generateAnswers(minAnswersPerQuestion, maxAnswersPerQuestion, question)
            }
        }
    }

    private fun generateQuestionnaires(
        questionnaireAmount: Int,
        principle: JWTPrincipal,
        faculties: List<MongoFaculty>,
        coursesOfStudies: List<MongoCourseOfStudies>
    ) = Array(questionnaireAmount) { index ->
        val randomFaculty = faculties.random()
        val randomCourseOfStudies = coursesOfStudies.filter { cos -> randomFaculty.id in cos.facultyIds }.random()

        MongoQuestionnaire(
            title = "Questionnaire Title No. $index",
            authorInfo = AuthorInfo(principle.userId, principle.userName),
            facultyIds = listOf(randomFaculty.id),
            courseOfStudiesIds = listOf(randomCourseOfStudies.id),
            subject = "IW"
        )
    }.toList()


    private fun generateQuestions(
        min: Int,
        max: Int
    ) = Array(Random.nextInt(max - min + 1) + min) { index ->
        MongoQuestion(
            questionText = "Question Text No. $index",
            questionPosition = index,
            isMultipleChoice = Random.nextBoolean()
        )
    }.toList()


    private fun generateAnswers(
        min: Int,
        max: Int,
        question: MongoQuestion
    ): List<MongoAnswer> {
        val answers = Array(Random.nextInt(max - min + 1) + min) { index ->
            MongoAnswer(answerText = "Answer Text No. $index", answerPosition = index)
        }.toList()

        if(question.isMultipleChoice){
            answers.onEach { it.isAnswerCorrect = Random.nextBoolean() }
            if (answers.none(MongoAnswer::isAnswerCorrect)) {
                answers.random().isAnswerCorrect = true
            }
        } else {
            answers.random().isAnswerCorrect = true
        }

        return answers
    }










    //TODO -> Hier gescheite Fragebögen erstellen fürs testen
    fun generateReadableQuestionnaires(principle: JWTPrincipal) : List<MongoQuestionnaire> {
        val questionnaireOne = generateReadableQuestionnaireOne(principle)

        return listOf(questionnaireOne)
    }

    private fun generateReadableQuestionnaireOne(principle: JWTPrincipal) : MongoQuestionnaire {
        val answersQuestionOne = listOf(
            MongoAnswer(answerText = "4", isAnswerCorrect = false),
            MongoAnswer(answerText = "5", isAnswerCorrect = false),
            MongoAnswer(answerText = "7", isAnswerCorrect = false),
            MongoAnswer(answerText = "9", isAnswerCorrect = true)
        )

        val questionOne = MongoQuestion(
            questionText = "Wie viele Fakultäten gibt es an der HFU?",
            questionPosition = 0,
            isMultipleChoice = false,
            answers = answersQuestionOne
        )


        val answersQuestionTwo = listOf(
            MongoAnswer(answerText = "45", isAnswerCorrect = false),
            MongoAnswer(answerText = "55", isAnswerCorrect = false),
            MongoAnswer(answerText = "65", isAnswerCorrect = true),
            MongoAnswer(answerText = "75", isAnswerCorrect = true)
        )

        val questionTwo = MongoQuestion(
            questionText = "Wie viele Studiengänge gibt es an der HFU?",
            questionPosition = 1,
            isMultipleChoice = false,
            answers = answersQuestionTwo
        )


        val answersQuestionThree = listOf(
            MongoAnswer(answerText = "Oder", isAnswerCorrect = false),
            MongoAnswer(answerText = "Donau", isAnswerCorrect = true),
            MongoAnswer(answerText = "Rhein", isAnswerCorrect = false)
        )

        val questionThree = MongoQuestion(
            questionText = "Welcher dieser Flüsse ist der längste Fluss Deutschlands?",
            questionPosition = 2,
            isMultipleChoice = false,
            answers = answersQuestionThree
        )


        val answersQuestionFour= listOf(
            MongoAnswer(answerText = "Rohstoffe", isAnswerCorrect = true),
            MongoAnswer(answerText = "Stromkosten der Produktion", isAnswerCorrect = false),
            MongoAnswer(answerText = "Lieferkosten", isAnswerCorrect = true),
            MongoAnswer(answerText = "Miete", isAnswerCorrect = false)
        )

        val questionFour = MongoQuestion(
            questionText = "Was zählt zu der variablen Kostenart?",
            questionPosition = 3,
            isMultipleChoice = true,
            answers = answersQuestionFour
        )


        val answersQuestionFive= listOf(
            MongoAnswer(answerText = "B-Rabbit", isAnswerCorrect = true),
            MongoAnswer(answerText = "Bizarre", isAnswerCorrect = false),
            MongoAnswer(answerText = "Shady", isAnswerCorrect = true),
            MongoAnswer(answerText = "Proof", isAnswerCorrect = false)
        )

        val questionFive = MongoQuestion(
            questionText = "Unter welchem dieser Namen ist der Rapper Eminem noch bekannt?",
            questionPosition = 4,
            isMultipleChoice = true,
            answers = answersQuestionFive
        )


        val answersQuestionSix = listOf(
            MongoAnswer(answerText = "1954", isAnswerCorrect = true),
            MongoAnswer(answerText = "1974", isAnswerCorrect = true),
            MongoAnswer(answerText = "1978", isAnswerCorrect = false),
            MongoAnswer(answerText = "1986", isAnswerCorrect = false),
            MongoAnswer(answerText = "1990", isAnswerCorrect = true)
        )

        val questionSix = MongoQuestion(
            questionText = "In welchen Jahren gewann Deutschland die WM im Fußball?",
            questionPosition = 5,
            isMultipleChoice = true,
            answers = answersQuestionSix
        )


        val answersQuestionSeven = listOf(
            MongoAnswer(answerText = "onCreate", isAnswerCorrect = true),
            MongoAnswer(answerText = "onWait", isAnswerCorrect = false),
            MongoAnswer(answerText = "onInitialized", isAnswerCorrect = false),
            MongoAnswer(answerText = "onResumed", isAnswerCorrect = true),
            MongoAnswer(answerText = "onRecreated", isAnswerCorrect = false)
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