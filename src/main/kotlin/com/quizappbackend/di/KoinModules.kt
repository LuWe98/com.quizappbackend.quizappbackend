package com.quizappbackend.di

import com.quizappbackend.model.mongodb.MongoRepository
import com.quizappbackend.model.mongodb.MongoRepositoryImpl
import com.quizappbackend.model.mongodb.dao.*
import com.quizappbackend.model.mongodb.documents.*
import com.quizappbackend.model.mongodb.properties.AuthorInfo
import com.quizappbackend.routing.services.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.runBlocking
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.div
import org.litote.kmongo.reactivestreams.KMongo

object KoinModules {

    private const val MONGO_DATABASE_NAME = "QuizDatabase"


    private val mongoModule = module {

        single {
            KMongo.createClient().coroutine
        }

        single {
            get<CoroutineClient>().getDatabase(MONGO_DATABASE_NAME)
        }

        single(named(User.COLLECTION_NAME), true) {
            get<CoroutineDatabase>().getCollection<User>(User.COLLECTION_NAME).apply {
                runBlocking(IO) {
                    ensureIndex(User::role)
                    ensureUniqueIndex(User::name)
                }
            }
        }

        single(named(MongoQuestionnaire.COLLECTION_NAME), true) {
            get<CoroutineDatabase>().getCollection<MongoQuestionnaire>(MongoQuestionnaire.COLLECTION_NAME).apply {
                runBlocking(IO) {
                    ensureIndex(MongoQuestionnaire::authorInfo / AuthorInfo::userId)
                    ensureIndex(MongoQuestionnaire::authorInfo / AuthorInfo::userName)
                    ensureIndex(MongoQuestionnaire::title)
                    ensureIndex(MongoQuestionnaire::lastModifiedTimestamp)
                    ensureIndex(MongoQuestionnaire::visibility)
                    ensureUniqueIndex(MongoQuestionnaire::lastModifiedTimestamp, MongoQuestionnaire::id)
                    ensureUniqueIndex(MongoQuestionnaire::title, MongoQuestionnaire::id)
                }
            }
        }

        single(named(MongoFilledQuestionnaire.COLLECTION_NAME), true) {
            get<CoroutineDatabase>().getCollection<MongoFilledQuestionnaire>(MongoFilledQuestionnaire.COLLECTION_NAME).apply {
                runBlocking(IO) {
                    ensureIndex(MongoFilledQuestionnaire::userId)
                    ensureUniqueIndex(MongoFilledQuestionnaire::questionnaireId, MongoFilledQuestionnaire::userId)
                }
            }
        }

        single(named(MongoFaculty.COLLECTION_NAME), true) {
            get<CoroutineDatabase>().getCollection<MongoFaculty>(MongoFaculty.COLLECTION_NAME).apply {
                runBlocking(IO) {
                    ensureUniqueIndex(MongoFaculty::name)
                    ensureUniqueIndex(MongoFaculty::abbreviation)
                }
            }
        }

        single(named(MongoCourseOfStudies.COLLECTION_NAME), true) {
            get<CoroutineDatabase>().getCollection<MongoCourseOfStudies>(MongoCourseOfStudies.COLLECTION_NAME).apply {
                runBlocking(IO) {
                    ensureIndex(MongoCourseOfStudies::name)
                    ensureIndex(MongoCourseOfStudies::abbreviation)
                    ensureUniqueIndex(MongoCourseOfStudies::name, MongoCourseOfStudies::abbreviation)
                }
            }
        }

        single<UserDao> { UserDaoImpl(collection = get(named(User.COLLECTION_NAME))) }

        single<QuestionnaireDao> { QuestionnaireDaoImpl(collection = get(named(MongoQuestionnaire.COLLECTION_NAME))) }

        single<FilledQuestionnaireDao> { FilledQuestionnaireDaoImpl(collection = get(named(MongoFilledQuestionnaire.COLLECTION_NAME))) }

        single<FacultyDao> { FacultyDaoImpl(collection = get(named(MongoFaculty.COLLECTION_NAME))) }

        single<CourseOfStudiesDao> { CourseOfStudiesDaoImpl(collection = get(named(MongoCourseOfStudies.COLLECTION_NAME))) }

        single<MongoRepository> {
            MongoRepositoryImpl(
                client = get(),
                userDao = get(),
                questionnaireDao = get(),
                filledQuestionnaireDao = get(),
                facultyDao = get(),
                courseOfStudiesDao = get()
            )
        }

    }

    private val routeModule = module {
        single<FacultyRouteService> { FacultyRouteServiceImpl(mongoRepository = get()) }

        single<CourseOfStudiesRouteService> { CourseOfStudiesRouteServiceImpl(mongoRepository = get()) }

        single<UserRouteService> { UserRouteServiceImpl(mongoRepository = get()) }

        single<FilledQuestionnaireRouteService> { FilledQuestionnaireRouteServiceImpl(mongoRepository = get()) }

        single<QuestionnaireRouteService> { QuestionnaireRouteServiceImpl(mongoRepository = get()) }
    }

    val modules = listOf(
        mongoModule,
        routeModule
    )
}