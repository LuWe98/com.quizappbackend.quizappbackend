package com.quizappbackend

import com.quizappbackend.authentication.JwtAuth.initJwtAuthentication
import com.quizappbackend.di.KoinModules
import com.quizappbackend.extensions.insertMany
import com.quizappbackend.extensions.isCollectionEmpty
import com.quizappbackend.model.ktor.registerStatusPages
import com.quizappbackend.model.mongodb.MongoRepository
import com.quizappbackend.model.mongodb.documents.MongoCourseOfStudies
import com.quizappbackend.model.mongodb.documents.MongoFaculty
import com.quizappbackend.model.mongodb.documents.User
import com.quizappbackend.routing.initRoutes
import com.quizappbackend.utils.DataPrefillUtil
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.netty.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.koin.ktor.ext.Koin
import org.koin.ktor.ext.getKoin
import org.slf4j.event.Level

fun main(args: Array<String>) = EngineMain.main(args)

@Suppress("unused")
fun Application.module() {

    install(CallLogging) {
        level = Level.INFO
    }

    install(DefaultHeaders)

    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
        })
    }

    install(StatusPages) {
        registerStatusPages()
    }

    install(Koin) {
        modules(KoinModules.modules)
    }

    install(Authentication) {
        initJwtAuthentication(getKoin().get())
    }

    install(Routing).initRoutes(
        facultyRouteService = getKoin().get(),
        courseOfStudiesRouteService = getKoin().get(),
        userRouteService = getKoin().get(),
        filledQuestionnaireRouteService = getKoin().get(),
        questionnaireRouteService = getKoin().get()
    )

    insertPrefillDataIfNeeded(getKoin().get())
}

private fun insertPrefillDataIfNeeded(mongoRepository: MongoRepository) = mongoRepository.apply {
    CoroutineScope(Dispatchers.IO).launch {
        if (isCollectionEmpty<MongoFaculty>() && isCollectionEmpty<MongoCourseOfStudies>()) {
            DataPrefillUtil.facultiesAndCoursesOfStudies.let { (faculties, coursesOfStudies) ->
                insertMany(faculties)
                insertMany(coursesOfStudies)
            }
        }

        if (isCollectionEmpty<User>()) {
            insertMany(DataPrefillUtil.professorListShortened)
        }
    }
}