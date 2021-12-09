package com.quizappbackend

import com.quizappbackend.authentication.JwtAuth.registerJwtAdminAuthentication
import com.quizappbackend.authentication.JwtAuth.registerJwtAuthentication
import com.quizappbackend.di.KoinModules
import com.quizappbackend.logging.registerStatusPages
import com.quizappbackend.model.databases.mongodb.MongoRepository
import com.quizappbackend.routing.*
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.netty.*
import kotlinx.serialization.json.Json
import org.koin.java.KoinJavaComponent
import org.koin.ktor.ext.Koin
import org.slf4j.event.Level

fun main(args: Array<String>) = EngineMain.main(args)

lateinit var mongoRepository: MongoRepository

@Suppress("unused")
fun Application.module() {

    install(DefaultHeaders)

    install(CallLogging) {
        level = Level.INFO
    }

    install(Koin) {
        modules(KoinModules.SINGLETON_MODULE)
        mongoRepository = KoinJavaComponent.get(MongoRepository::class.java)
    }

    install(Authentication) {
        registerJwtAuthentication()
        registerJwtAdminAuthentication()
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

    install(Routing) {
        registerUserRoutes()
        registerQuestionnaireRoutes()
        registerFilledQuestionnaireRoutes()
        registerFacultyRoutes()
        registerCourseOfStudiesRoutes()
    }
}