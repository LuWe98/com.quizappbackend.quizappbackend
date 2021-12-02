package com.quizappbackend.authentication

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.quizappbackend.authentication.UserCredentialsErrorType.CREDENTIALS_CHANGED
import com.quizappbackend.authentication.UserCredentialsErrorType.USER_DOES_NOT_EXIST
import com.quizappbackend.model.databases.mongodb.documents.user.Role
import com.quizappbackend.model.databases.mongodb.documents.user.User
import com.quizappbackend.mongoRepository
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.AuthenticationFailedCause.*
import io.ktor.auth.jwt.*
import io.ktor.util.date.*
import io.ktor.util.pipeline.*
import java.util.*
import java.util.concurrent.TimeUnit

object JwtAuth {

    private const val REALM = "QuizAppRealm"
    private const val SUBJECT = "JWTAuthentication"
    private const val ISSUER = "QuizAppBackend"
    private const val SECRET = "My Secret"

//    private val REALM = System.getenv()["JWT_REALM"]!!
//    private val SUBJECT = System.getenv()["JWT_SUBJECT"]!!
//    private val ISSUER = System.getenv()["JWT_ISSUER"]!!
//    private val SECRET = System.getenv()["JWT_SECRET"]!!
    private val ALGORITHM: Algorithm = Algorithm.HMAC512(SECRET)
    private val expirationDate get() = Date(getTimeMillis() + TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS))

    private const val CLAIM_USER_ID = "userId"
    private const val CLAIM_USER_NAME = "userName"
    private const val CLAIM_USER_ROLE = "userRole"


    private const val ERROR_CAUSE_USER_CREDENTIALS_CHANGED = "errorCauseUserCredentialsChanged"
    private const val ERROR_CAUSE_USER_DOES_NOT_EXIST = "errorCauseUserDoesNotExists"
    private const val ERROR_CAUSE_USER_ROLE_CHANGED = "errorCauseUserRoleChanged"

    const val ADMIN_ROUTE = "adminRoute"

    private val PipelineContext<*, ApplicationCall>.userPrincipleOptional get() = call.principal<JWTPrincipal>()

    val PipelineContext<*, ApplicationCall>.userPrinciple get() = userPrincipleOptional!!

    val JWTCredential.userId: String get() = payload.getClaim(CLAIM_USER_ID).asString()

    val JWTCredential.userName: String get() = payload.getClaim(CLAIM_USER_NAME).asString()

    val JWTCredential.userRole: Role get() = Role.valueOf(payload.getClaim(CLAIM_USER_ROLE).asString())

    val JWTPrincipal.userId: String get() = payload.getClaim(CLAIM_USER_ID).asString()

    val JWTPrincipal.userName: String get() = payload.getClaim(CLAIM_USER_NAME).asString()

    val JWTPrincipal.userRole: Role get() = Role.valueOf(payload.getClaim(CLAIM_USER_ROLE).asString())


    fun generateToken(user: User): String = JWT.create()
        .withSubject(SUBJECT)
        .withIssuer(ISSUER)
        .withClaim(CLAIM_USER_ID, user.id)
        .withClaim(CLAIM_USER_NAME, user.userName)
        .withClaim(CLAIM_USER_ROLE, user.role.name)
        .withExpiresAt(expirationDate)
        .sign(ALGORITHM)

    private val jwtVerifier: JWTVerifier = JWT
            .require(ALGORITHM)
            .withIssuer(ISSUER)
            .build()


    fun Authentication.Configuration.registerJwtAuthentication() = jwt {
        realm = REALM
        verifier(jwtVerifier)

        validate { credentials ->
            mongoRepository.findOneById<User>(credentials.userId).let { user ->
                if (user == null) {
                    authentication.error(ERROR_CAUSE_USER_DOES_NOT_EXIST, Error(ERROR_CAUSE_USER_DOES_NOT_EXIST))
                    return@let null
                }

                if (user.userName != credentials.userName) {
                    authentication.error(ERROR_CAUSE_USER_CREDENTIALS_CHANGED, Error(ERROR_CAUSE_USER_CREDENTIALS_CHANGED))
                    return@let null
                }

                if(user.role != credentials.userRole){
                    authentication.error(ERROR_CAUSE_USER_ROLE_CHANGED, Error(ERROR_CAUSE_USER_ROLE_CHANGED))
                    return@let null
                }

                JWTPrincipal(credentials.payload)
            }
        }

        initChallenge()
    }

    fun Authentication.Configuration.registerJwtAdminAuthentication() = jwt(ADMIN_ROUTE) {
        realm = REALM
        verifier(jwtVerifier)

        validate { credentials ->
            mongoRepository.findOneById<User>(credentials.userId).let { user ->
                if (user == null) {
                    authentication.error(ERROR_CAUSE_USER_DOES_NOT_EXIST, Error(ERROR_CAUSE_USER_DOES_NOT_EXIST))
                    return@let null
                }

                if (user.userName != credentials.userName) {
                    authentication.error(ERROR_CAUSE_USER_CREDENTIALS_CHANGED, Error(ERROR_CAUSE_USER_CREDENTIALS_CHANGED))
                    return@let null
                }

                if(user.role != credentials.userRole){
                    authentication.error(ERROR_CAUSE_USER_ROLE_CHANGED, Error(ERROR_CAUSE_USER_ROLE_CHANGED))
                    return@let null
                }

                if (user.role != Role.ADMIN) {
                    return@let null
                }

                JWTPrincipal(credentials.payload)
            }
        }

        initChallenge()
    }

    private fun JWTAuthenticationProvider.Configuration.initChallenge(){
        challenge { schema, _ ->

            if(call.authentication.allErrors.isEmpty() || call.authentication.allErrors.any{ it.message == ERROR_CAUSE_USER_ROLE_CHANGED }){
                throw UnauthorizedException(schema, realm)
            }

            if(call.authentication.allErrors.any { it.message == ERROR_CAUSE_USER_CREDENTIALS_CHANGED }){
                throw UserCredentialsChangedException(CREDENTIALS_CHANGED)
            }

            if(call.authentication.allErrors.any { it.message == ERROR_CAUSE_USER_DOES_NOT_EXIST }){
                throw UserCredentialsChangedException(USER_DOES_NOT_EXIST)
            }
        }
    }
}