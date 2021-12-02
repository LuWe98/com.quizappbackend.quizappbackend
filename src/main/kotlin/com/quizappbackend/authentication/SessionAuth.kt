package com.quizappbackend.authentication

import com.quizappbackend.authentication.JwtAuth.userId
import com.quizappbackend.model.databases.mongodb.documents.user.User
import com.quizappbackend.mongoRepository
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.http.*
import io.ktor.response.*

object SessionAuth {
    fun Authentication.Configuration.registerSession() = session<JWTPrincipal>("auth-session") {
        validate { session ->
            if (mongoRepository.checkIfExistsWithId<User>(session.userId)) session else null
        }

        challenge {
            call.respond(HttpStatusCode.Unauthorized)
        }
    }
}