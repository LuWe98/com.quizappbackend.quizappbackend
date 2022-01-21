package com.quizappbackend.routing.services

import com.quizappbackend.model.ktor.BackendRequest.*
import com.quizappbackend.model.ktor.BackendResponse.*
import com.quizappbackend.model.mongodb.dto.MongoBrowsableQuestionnaire
import io.ktor.auth.jwt.*

interface QuestionnaireRouteService {

    suspend fun handleSyncQuestionnaireRequest(principle: JWTPrincipal, request: SyncQuestionnairesRequest): SyncQuestionnairesResponse

    suspend fun handleInsertRequest(request: InsertQuestionnairesRequest): InsertQuestionnairesResponse

    suspend fun handleDeleteRequest(request: DeleteQuestionnaireRequest): DeleteQuestionnaireResponse

    suspend fun handleGetPagesQuestionnairesRequest(principle: JWTPrincipal, request: GetPagedQuestionnairesRequest): List<MongoBrowsableQuestionnaire>

    suspend fun handleDownloadQuestionnaireRequest(principle: JWTPrincipal, request: GetQuestionnaireRequest): GetQuestionnaireResponse

    suspend fun handleChangeVisibilityRequest(request: ChangeQuestionnaireVisibilityRequest): ChangeQuestionnaireVisibilityResponse

    suspend fun handleShareRequest(request: ShareQuestionnaireWithUserRequest): ShareQuestionnaireWithUserResponse




    suspend fun handleGenerateRandomQuestionnairesRequest(principle: JWTPrincipal, amount: String): Boolean
}