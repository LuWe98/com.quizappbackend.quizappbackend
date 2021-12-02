package com.quizappbackend.utils

import com.quizappbackend.model.databases.mongodb.documents.questionnaire.MongoQuestionnaire
import com.quizappbackend.model.databases.mongodb.documents.questionnairefilled.MongoFilledQuestion
import com.quizappbackend.model.databases.mongodb.documents.questionnairefilled.MongoFilledQuestionnaire
import kotlin.random.Random

object RandomFilledQuestionnaireCreatorUtil {

    fun generateRandomData(
        userId: String,
        mongoQuestionnaires : List<MongoQuestionnaire>,
    ): List<MongoFilledQuestionnaire> {
        return mongoQuestionnaires.map {
            MongoFilledQuestionnaire(it.id, userId).apply {
                val mongoFilledQuestion = it.questions.map { question ->
                    val filledAnswers = question.answers.filter { Random.nextBoolean() }.map { answer -> answer.id }
                    MongoFilledQuestion(questionId = question.id, selectedAnswerIds = filledAnswers)
                }
                questions = mongoFilledQuestion
            }
        }.toList()
    }
}