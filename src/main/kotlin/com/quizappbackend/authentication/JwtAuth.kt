package com.quizappbackend.authentication

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.quizappbackend.authentication.UserCredentialsErrorType.CREDENTIALS_CHANGED
import com.quizappbackend.authentication.UserCredentialsErrorType.USER_DOES_NOT_EXIST
import com.quizappbackend.extensions.findOneById
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

    private const val CLAIM_USER_ID = "userIdClaim"
    private const val CLAIM_USER_ROLE = "userRoleClaim"
    private const val CLAIM_LAST_MODIFIED_TIMESTAMP = "lastModifiedTimestampClaim"
    private const val CLAIM_CAN_SHARE_QUESTIONNAIRE_WITH = "canShareQuestionnaireWithClaim"

    private const val ERROR_CAUSE_USER_CREDENTIALS_CHANGED = "errorCauseUserCredentialsChanged"
    private const val ERROR_CAUSE_USER_DOES_NOT_EXIST = "errorCauseUserDoesNotExists"
    private const val ERROR_CAUSE_USER_ROLE_CHANGED = "errorCauseUserRoleChanged"
    private const val ERROR_CAUSE_USER_CAN_SHARE_QUESTIONNAIRE_WITH_CHANGED = "errorCauseUserCanShareQuestionnaireWithChanged"

    fun generateToken(user: User): String = generateToken(
        user.id,
        user.role,
        user.lastModifiedTimestamp,
        user.canShareQuestionnairesWith
    )

    private fun generateToken(
        userId: String,
        role: Role,
        timeStamp: Long,
        canShareQuestionnaireWith: Boolean
    ): String = JWT
        .create()
        .withSubject(SUBJECT)
        .withIssuer(ISSUER)
        .withClaim(CLAIM_USER_ID, userId)
        .withClaim(CLAIM_USER_ROLE, role.name)
        .withClaim(CLAIM_LAST_MODIFIED_TIMESTAMP, timeStamp)
        .withClaim(CLAIM_CAN_SHARE_QUESTIONNAIRE_WITH, canShareQuestionnaireWith)
        .withExpiresAt(expirationDate)
        .sign(ALGORITHM)

    private val jwtVerifier: JWTVerifier = JWT
        .require(ALGORITHM)
        .withIssuer(ISSUER)
        .build()


    val PipelineContext<*, ApplicationCall>.userPrinciple get() = call.principal<JWTPrincipal>()!!

    val JWTCredential.userId: String get() = payload.getClaim(CLAIM_USER_ID).asString()

    private val JWTCredential.lastModifiedTimestamp: Long get() = payload.getClaim(CLAIM_LAST_MODIFIED_TIMESTAMP).asLong()

    private val JWTCredential.canShareQuestionnaireWith: Boolean get() = payload.getClaim(CLAIM_CAN_SHARE_QUESTIONNAIRE_WITH).asBoolean()

    val JWTCredential.userRole: Role get() = Role.valueOf(payload.getClaim(CLAIM_USER_ROLE).asString())

    val JWTPrincipal.userId: String get() = payload.getClaim(CLAIM_USER_ID).asString()

    val JWTPrincipal.userRole: Role get() = Role.valueOf(payload.getClaim(CLAIM_USER_ROLE).asString())

    val JWTPrincipal.canShareQuestionnaireWith: Boolean get() = payload.getClaim(CLAIM_CAN_SHARE_QUESTIONNAIRE_WITH).asBoolean()


    fun Authentication.Configuration.initJwtAuthentication(mongoRepository: MongoRepository) = Role.values().forEach { role ->
        initAuthentication(this, mongoRepository, role)
    }

    private fun initAuthentication(config: Authentication.Configuration, mongoRepository: MongoRepository, roleScope: Role) = config.jwt(roleScope.name) {
        realm = REALM
        verifier(jwtVerifier)

        validate { credentials ->
            mongoRepository.findOneById<User>(credentials.userId).let { user ->
                if (user == null) {
                    authentication.error(ERROR_CAUSE_USER_DOES_NOT_EXIST, Error(ERROR_CAUSE_USER_DOES_NOT_EXIST))
                    return@let null
                }

                if (user.lastModifiedTimestamp != credentials.lastModifiedTimestamp) {
                    authentication.error(ERROR_CAUSE_USER_CREDENTIALS_CHANGED, Error(ERROR_CAUSE_USER_CREDENTIALS_CHANGED))
                    return@let null
                }

                if (user.role != credentials.userRole) {
                    authentication.error(ERROR_CAUSE_USER_ROLE_CHANGED, Error(ERROR_CAUSE_USER_ROLE_CHANGED))
                    return@let null
                }

                if(user.canShareQuestionnairesWith != credentials.canShareQuestionnaireWith) {
                    authentication.error(ERROR_CAUSE_USER_CAN_SHARE_QUESTIONNAIRE_WITH_CHANGED, Error(ERROR_CAUSE_USER_CAN_SHARE_QUESTIONNAIRE_WITH_CHANGED))
                    return@let null
                }

                if (!user.role.hasPrivilege(roleScope)) {
                    return@let null
                }

                JWTPrincipal(credentials.payload)
            }
        }

        challenge { schema, _ ->
            if (call.authentication.allErrors.any { error -> error.message == ERROR_CAUSE_USER_CREDENTIALS_CHANGED }) {
                throw UserCredentialsChangedException(CREDENTIALS_CHANGED)
            }

            if (call.authentication.allErrors.any { error -> error.message == ERROR_CAUSE_USER_DOES_NOT_EXIST }) {
                throw UserCredentialsChangedException(USER_DOES_NOT_EXIST)
            }

            if (call.authentication.allErrors.isEmpty() || call.authentication.allErrors.any { error ->
                    error.message == ERROR_CAUSE_USER_ROLE_CHANGED || error.message == ERROR_CAUSE_USER_CAN_SHARE_QUESTIONNAIRE_WITH_CHANGED
                }) {
                throw UserUnauthorizedException(schema, realm)
            }
        }
    }
}