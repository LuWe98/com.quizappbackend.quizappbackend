package com.quizappbackend.utils

import com.quizappbackend.authentication.JwtAuth.userId
import com.quizappbackend.extensions.findOneById
import com.quizappbackend.extensions.getAllEntries
import com.quizappbackend.model.mongodb.MongoRepository
import com.quizappbackend.model.mongodb.documents.*
import com.quizappbackend.model.mongodb.properties.AuthorInfo
import com.quizappbackend.model.mongodb.properties.QuestionnaireVisibility
import io.ktor.auth.jwt.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import kotlin.random.Random

object RandomQuestionnaireCreationUtil {

    suspend fun generateRandomData(
        mongoRepository: MongoRepository,
        principle: JWTPrincipal,
        questionnaireAmount: Int,
        minQuestionsPerQuestionnaire: Int,
        maxQuestionsPerQuestionnaire: Int,
        minAnswersPerQuestion: Int,
        maxAnswersPerQuestion: Int
    ): List<MongoQuestionnaire> = withContext(IO) {

        val currentUser = mongoRepository.findOneById<User>(principle.userId)

        if (maxQuestionsPerQuestionnaire < minQuestionsPerQuestionnaire)
            throw IllegalArgumentException("'maxQuestionsPerQuestionnaire' has to be bigger than 'minQuestionsPerQuestionnaire'")

        if (maxAnswersPerQuestion < minAnswersPerQuestion)
            throw IllegalArgumentException("'maxAnswersPerQuestion' has to be bigger than 'minAnswersPerQuestion'")

        val faculties = async { mongoRepository.getAllEntries<MongoFaculty>() }
        val coursesOfStudies = async { mongoRepository.getAllEntries<MongoCourseOfStudies>() }

        return@withContext List(questionnaireAmount) { index ->
            val randomFaculty = faculties.await().random()
            val randomCourseOfStudies = coursesOfStudies.await().filter { cos -> randomFaculty.id in cos.facultyIds }.random()
            val generatedQuestions = generateQuestions(
                minQuestionsPerQuestionnaire,
                maxQuestionsPerQuestionnaire,
                minAnswersPerQuestion,
                maxAnswersPerQuestion
            )

            MongoQuestionnaire(
                title = "Questionnaire Title No. $index",
                authorInfo = AuthorInfo(principle.userId, currentUser!!.name),
                facultyIds = listOf(randomFaculty.id),
                courseOfStudiesIds = listOf(randomCourseOfStudies.id),
                subject = "IW",
                visibility = QuestionnaireVisibility.PUBLIC,
                questionCount = generatedQuestions.size,
                questions = generatedQuestions
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

    fun generateRandomFilledQuestionnaires(
        userId: String,
        mongoQuestionnaires: List<MongoQuestionnaire>,
    ) = mongoQuestionnaires.map {
        MongoFilledQuestionnaire(
            questionnaireId = it.id,
            userId = userId,
            questions = it.questions.map { question ->
                val filledAnswers = question.answers.filter { Random.nextBoolean() }.map { answer -> answer.id }
                MongoFilledQuestion(questionId = question.id, selectedAnswerIds = filledAnswers)
            }
        )
    }.toList()
}