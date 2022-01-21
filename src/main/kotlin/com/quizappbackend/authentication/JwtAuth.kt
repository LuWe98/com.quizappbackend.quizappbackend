package com.quizappbackend.authentication

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.quizappbackend.authentication.UserCredentialsErrorType.CREDENTIALS_CHANGED
import com.quizappbackend.authentication.UserCredentialsErrorType.USER_DOES_NOT_EXIST
import com.quizappbackend.model.mongodb.MongoRepository
import com.quizappbackend.model.mongodb.documents.User
import com.quizappbackend.model.mongodb.properties.Role
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.AuthenticationFailedCause.*
import io.ktor.auth.jwt.*
import io.ktor.util.date.*
import io.ktor.util.pipeline.*
import java.util.*
import java.util.concurrent.TimeUnit

object JwtAuth {

    const val ADMIN_ROUTE = "adminRoute"

    private const val REALM = "QuizAppRealm"
    private const val SUBJECT = "JWTAuthentication"
    private const val ISSUER = "QuizAppBackend"
    private const val SECRET = "167d7dda07649ad1a58486c28042a685b128125923cecd14c14dd7aa70027452"

    //TODO -> Später über environment Variablen die Daten holen
//    private val REALM = System.getenv()["JWT_REALM"]!!
//    private val SUBJECT = System.getenv()["JWT_SUBJECT"]!!
//    private val ISSUER = System.getenv()["JWT_ISSUER"]!!
//    private val SECRET = System.getenv()["JWT_SECRET"]!!

    private val ALGORITHM: Algorithm = Algorithm.HMAC512(SECRET)
    private val expirationDate get() = Date(getTimeMillis() + TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS))

    private const val CLAIM_USER_ID = "userId"
    private const val CLAIM_USER_NAME = "userName"
    private const val CLAIM_HASHED_PW = "hashedPw"
    private const val CLAIM_USER_ROLE = "userRole"

    private const val ERROR_CAUSE_USER_CREDENTIALS_CHANGED = "errorCauseUserCredentialsChanged"
    private const val ERROR_CAUSE_USER_DOES_NOT_EXIST = "errorCauseUserDoesNotExists"
    private const val ERROR_CAUSE_USER_ROLE_CHANGED = "errorCauseUserRoleChanged"

    fun generateToken(user: User): String = generateToken(
        user.id,
        user.userName,
        user.password,
        user.role
    )

    private fun generateToken(
        userId: String,
        userName: String,
        hashedPw: String,
        role: Role
    ): String = JWT
        .create()
        .withSubject(SUBJECT)
        .withIssuer(ISSUER)
        .withClaim(CLAIM_USER_ID, userId)
        .withClaim(CLAIM_USER_NAME, userName)
        .withClaim(CLAIM_HASHED_PW, hashedPw)
        .withClaim(CLAIM_USER_ROLE, role.name)
        .withExpiresAt(expirationDate)
        .sign(ALGORITHM)

    private val jwtVerifier: JWTVerifier = JWT
        .require(ALGORITHM)
        .withIssuer(ISSUER)
        .build()


    private val PipelineContext<*, ApplicationCall>.userPrincipleOptional get() = call.principal<JWTPrincipal>()

    val PipelineContext<*, ApplicationCall>.userPrinciple get() = userPrincipleOptional!!

    val JWTCredential.userId: String get() = payload.getClaim(CLAIM_USER_ID).asString()

    val JWTCredential.userName: String get() = payload.getClaim(CLAIM_USER_NAME).asString()

    private val JWTCredential.userHashedPassword: String get() = payload.getClaim(CLAIM_HASHED_PW).asString()

    val JWTCredential.userRole: Role get() = Role.valueOf(payload.getClaim(CLAIM_USER_ROLE).asString())

    val JWTPrincipal.userId: String get() = payload.getClaim(CLAIM_USER_ID).asString()

    val JWTPrincipal.userName: String get() = payload.getClaim(CLAIM_USER_NAME).asString()

    val JWTPrincipal.userRole: Role get() = Role.valueOf(payload.getClaim(CLAIM_USER_ROLE).asString())


    fun Authentication.Configuration.initJwtAuthentication(mongoRepository: MongoRepository) {
        jwt(ADMIN_ROUTE) { initAuthentication(mongoRepository, true) }
        jwt { initAuthentication(mongoRepository) }
    }

    private fun JWTAuthenticationProvider.Configuration.initAuthentication(mongoRepository: MongoRepository, adminAuthentication: Boolean = false) {
        realm = REALM
        verifier(jwtVerifier)

        validate { credentials ->
            mongoRepository.findOneById<User>(credentials.userId).let { user ->
                if (user == null) {
                    authentication.error(ERROR_CAUSE_USER_DOES_NOT_EXIST, Error(ERROR_CAUSE_USER_DOES_NOT_EXIST))
                    return@let null
                }

                if (user.userName != credentials.userName || user.password != credentials.userHashedPassword) {
                    authentication.error(ERROR_CAUSE_USER_CREDENTIALS_CHANGED, Error(ERROR_CAUSE_USER_CREDENTIALS_CHANGED))
                    return@let null
                }

                if (user.role != credentials.userRole) {
                    authentication.error(ERROR_CAUSE_USER_ROLE_CHANGED, Error(ERROR_CAUSE_USER_ROLE_CHANGED))
                    return@let null
                }

                if (adminAuthentication && user.role != Role.ADMIN) {
                    return@let null
                }

                JWTPrincipal(credentials.payload)
            }
        }

        challenge { schema, _ ->
            if (call.authentication.allErrors.isEmpty() || call.authentication.allErrors.any { it.message == ERROR_CAUSE_USER_ROLE_CHANGED }) {
                throw UserUnauthorizedException(schema, realm)
            }

            if (call.authentication.allErrors.any { it.message == ERROR_CAUSE_USER_CREDENTIALS_CHANGED }) {
                throw UserCredentialsChangedException(CREDENTIALS_CHANGED)
            }

            if (call.authentication.allErrors.any { it.message == ERROR_CAUSE_USER_DOES_NOT_EXIST }) {
                throw UserCredentialsChangedException(USER_DOES_NOT_EXIST)
            }
        }
    }
}