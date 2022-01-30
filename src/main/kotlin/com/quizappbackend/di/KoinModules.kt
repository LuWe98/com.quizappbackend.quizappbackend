package com.quizappbackend.di

import com.quizappbackend.model.mongodb.MongoRepository
import com.quizappbackend.model.mongodb.dao.*
import com.quizappbackend.model.mongodb.documents.*
import com.quizappbackend.model.mongodb.properties.AuthorInfo
import com.quizappbackend.routing.services.*
import com.quizappbackend.utils.Constants
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.runBlocking
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.div
import org.litote.kmongo.reactivestreams.KMongo

object KoinModules {

    val SINGLETON_MODULE = module {

        single {
            KMongo.createClient().coroutine.getDatabase(Constants.MONGO_DATABASE_NAME)
        }

        single(named(Constants.MONGO_USER_COLLECTION_NAME), true) {
            get<CoroutineDatabase>().getCollection<User>(Constants.MONGO_USER_COLLECTION_NAME).apply {
                runBlocking(IO) {
                    ensureUniqueIndex(User::userName)
                    ensureIndex(User::role)
                }
            }
        }

        single(named(Constants.MONGO_QUESTIONNAIRE_COLLECTION_NAME), true) {
            get<CoroutineDatabase>().getCollection<MongoQuestionnaire>(Constants.MONGO_QUESTIONNAIRE_COLLECTION_NAME).apply {
                runBlocking(IO) {
                    ensureIndex(MongoQuestionnaire::authorInfo / AuthorInfo::userName)
                    ensureIndex(MongoQuestionnaire::authorInfo / AuthorInfo::userId)
                    ensureIndex(MongoQuestionnaire::title)
                    ensureIndex(MongoQuestionnaire::lastModifiedTimestamp)
                    ensureIndex(MongoQuestionnaire::title, MongoQuestionnaire::id)
                    ensureIndex(MongoQuestionnaire::lastModifiedTimestamp, MongoQuestionnaire::id)
                }
            }
        }

        single(named(Constants.MONGO_FILLED_QUESTIONNAIRE_COLLECTION_NAME), true) {
            get<CoroutineDatabase>().getCollection<MongoFilledQuestionnaire>(Constants.MONGO_FILLED_QUESTIONNAIRE_COLLECTION_NAME).apply {
                runBlocking(IO) {
                    ensureUniqueIndex(MongoFilledQuestionnaire::questionnaireId, MongoFilledQuestionnaire::userId)
                }
            }
        }

        single(named(Constants.MONGO_FACULTY_COLLECTION_NAME), true) {
            get<CoroutineDatabase>().getCollection<MongoFaculty>(Constants.MONGO_FACULTY_COLLECTION_NAME).apply {
                runBlocking(IO) {
                    ensureUniqueIndex(MongoFaculty::name)
                    ensureUniqueIndex(MongoFaculty::abbreviation)
                }
            }
        }

        single(named(Constants.MONGO_COURSE_OF_STUDIES_COLLECTION_NAME), true) {
            get<CoroutineDatabase>().getCollection<MongoCourseOfStudies>(Constants.MONGO_COURSE_OF_STUDIES_COLLECTION_NAME).apply {
                runBlocking(IO) {
                    ensureUniqueIndex(MongoCourseOfStudies::name, MongoCourseOfStudies::abbreviation)
                    ensureIndex(MongoCourseOfStudies::lastModifiedTimestamp)
                }
            }
        }

        single<UserDao> { UserDaoImpl(get(named(Constants.MONGO_USER_COLLECTION_NAME))) }

        single<QuestionnaireDao> { QuestionnaireDaoImpl(get(named(Constants.MONGO_QUESTIONNAIRE_COLLECTION_NAME))) }

        single<FilledQuestionnaireDao> { FilledQuestionnaireDaoImpl(get(named(Constants.MONGO_FILLED_QUESTIONNAIRE_COLLECTION_NAME))) }

        single<FacultyDao> { FacultyDaoImpl(get(named(Constants.MONGO_FACULTY_COLLECTION_NAME))) }

        single<CourseOfStudiesDao> { CourseOfStudiesDaoImpl(get(named(Constants.MONGO_COURSE_OF_STUDIES_COLLECTION_NAME))) }

        single { MongoRepository(get(), get(), get(), get(), get()) }

        single<FacultyRouteService> { FacultyRouteServiceImpl(get()) }

        single<CourseOfStudiesRouteService> { CourseOfStudiesRouteServiceImpl(get()) }

        single<UserRouteService> { UserRouteServiceImpl(get()) }

        single<FilledQuestionnaireRouteService> { FilledQuestionnaireRouteServiceImpl(get()) }

        single<QuestionnaireRouteService> { QuestionnaireRouteServiceImpl(get()) }

    }
}