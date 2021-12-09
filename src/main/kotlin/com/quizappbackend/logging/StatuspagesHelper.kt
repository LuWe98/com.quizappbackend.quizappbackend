package com.quizappbackend.logging

import com.quizappbackend.authentication.UnauthorizedException
import com.quizappbackend.authentication.UserCredentialsChangedException
import com.quizappbackend.utils.logError
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.auth.*
import io.ktor.response.*

fun StatusPages.Configuration.registerStatusPages() {

    exception<ContentTransformationException> { exception ->
        logError(exception)
        call.respond(HttpStatusCode.BadRequest)
    }

    exception<NotFoundException> { exception ->
        logError(exception)
        call.respond(HttpStatusCode.NotFound)
    }

    exception<BadRequestException> { exception ->
        logError(exception)
        call.respond(HttpStatusCode.BadRequest)
    }

    exception<UserCredentialsChangedException> { exception ->
        call.respond(HttpStatusCode.BadRequest, exception.userInfoError)
    }

    exception<UnauthorizedException> { exception ->
        call.respond(
            UnauthorizedResponse(
                HttpAuthHeader.Parameterized(
                    exception.schema,
                    mapOf(HttpAuthHeader.Parameters.Realm to exception.realm)
                )
            )
        )
    }

    exception<Exception> { exception ->
        logError(exception)
        call.respond(HttpStatusCode.InternalServerError)
    }
}