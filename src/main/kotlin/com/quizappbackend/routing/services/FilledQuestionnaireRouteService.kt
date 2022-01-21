package com.quizappbackend.routing.services

import com.quizappbackend.model.ktor.BackendRequest.*
import com.quizappbackend.model.ktor.BackendResponse.*
import io.ktor.auth.jwt.*

interface FilledQuestionnaireRouteService {

    suspend fun handleInsertRequest(principle: JWTPrincipal, request: InsertFilledQuestionnaireRequest) : InsertFilledQuestionnaireResponse

    suspend fun handleInsertMultipleRequest(principle: JWTPrincipal, request: InsertFilledQuestionnairesRequest): InsertFilledQuestionnairesResponse

    suspend fun handleDeleteRequest(principle: JWTPrincipal, request: DeleteFilledQuestionnaireRequest): DeleteFilledQuestionnaireResponse

    suspend fun handleGenerateRandomRequest(principle: JWTPrincipal, amount: String): Boolean

}