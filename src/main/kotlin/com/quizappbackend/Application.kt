package com.quizappbackend

import com.quizappbackend.authentication.JwtAuth.registerJwtAdminAuthentication
import com.quizappbackend.authentication.JwtAuth.registerJwtAuthentication
import com.quizappbackend.di.KoinModules
import com.quizappbackend.model.mongodb.MongoRepository
import com.quizappbackend.model.ktor.registerStatusPages
import com.quizappbackend.routing.*
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.netty.*
import kotlinx.serialization.json.Json
import org.koin.ktor.ext.Koin
import org.koin.ktor.ext.get
import org.slf4j.event.Level

lateinit var mongoRepository: MongoRepository

fun main(args: Array<String>) = EngineMain.main(args)

@Suppress("unused")
fun Application.module() {

    install(DefaultHeaders)

    install(CallLogging) {
        level = Level.INFO
    }

    install(Koin) {
        modules(KoinModules.SINGLETON_MODULE)
        mongoRepository = get()
    }

    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
        })
    }

    install(StatusPages) {
        registerStatusPages()
    }

    install(Authentication) {
        registerJwtAuthentication()
        registerJwtAdminAuthentication()
    }

    install(Routing) {
        registerUserRoutes()
        registerQuestionnaireRoutes()
        registerFilledQuestionnaireRoutes()
        registerFacultyRoutes()
        registerCourseOfStudiesRoutes()
    }
}