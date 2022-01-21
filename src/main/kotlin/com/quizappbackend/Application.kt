package com.quizappbackend

import com.quizappbackend.authentication.JwtAuth.initJwtAuthentication
import com.quizappbackend.di.KoinModules
import com.quizappbackend.model.ktor.registerStatusPages
import com.quizappbackend.routing.initRoutes
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.netty.*
import kotlinx.serialization.json.Json
import org.koin.ktor.ext.Koin
import org.koin.ktor.ext.getKoin
import org.slf4j.event.Level

fun main(args: Array<String>) = EngineMain.main(args)

@Suppress("unused")
fun Application.module() {

    install(DefaultHeaders)

    install(CallLogging) {
        level = Level.INFO
    }

    install(Koin) {
        modules(KoinModules.SINGLETON_MODULE)
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
        initJwtAuthentication(getKoin().get())
    }

    install(Routing).initRoutes(
        facultyRouteService = getKoin().get(),
        courseOfStudiesRouteService = getKoin().get(),
        userRouteService = getKoin().get(),
        filledQuestionnaireRouteService = getKoin().get(),
        questionnaireRouteService = getKoin().get()
    )
}