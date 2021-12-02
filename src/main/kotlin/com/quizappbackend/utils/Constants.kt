package com.quizappbackend.utils

// Für physisches Handy 192.168.178.78 -> CMD -> ipconfig
// Für Emulator Handy 10.0.2.2

object Constants {
    const val MONGO_DATABASE_NAME = "QuizDatabase"
    const val MONGO_USER_COLLECTION_NAME = "Users"
    const val MONGO_QUESTIONNAIRE_COLLECTION_NAME = "Questionnaires"
    const val MONGO_FILLED_QUESTIONNAIRE_COLLECTION_NAME = "FilledQuestionnaires"
    const val MONGO_COURSE_OF_STUDIES_COLLECTION_NAME = "CourseOfStudies"
    const val MONGO_FACULTY_COLLECTION_NAME = "Faculty"
}