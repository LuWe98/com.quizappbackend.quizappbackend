package com.quizappbackend.routing.services

import com.quizappbackend.model.ktor.BackendRequest.*
import com.quizappbackend.model.ktor.BackendResponse.*
import com.quizappbackend.model.mongodb.documents.User
import com.quizappbackend.model.mongodb.properties.AuthorInfo
import io.ktor.auth.jwt.*

interface UserRouteService {

    suspend fun handleRegisterUserRequest(request: RegisterUserRequest): RegisterUserResponse

    suspend fun handleLoginUserRequest(request: LoginUserRequest) : LoginUserResponse

    suspend fun handleCreateUserRequest(request: CreateUserRequest): CreateUserResponse

    suspend fun handleGetRefreshTokenRequest(request: RefreshJwtTokenRequest): RefreshJwtTokenResponse

    suspend fun handleSyncUserDataRequest(principle: JWTPrincipal, request: SyncUserDataRequest): SyncUserDataResponse

    suspend fun handleUpdateUserNameRequest(principle: JWTPrincipal, request: UpdateUserNameRequest): UpdateUserResponse

    suspend fun handleUpdatePasswordRequest(principle: JWTPrincipal, request: ChangePasswordRequest): ChangePasswordResponse

    suspend fun handleUpdateUserRoleRequest(request: UpdateUserRoleRequest): UpdateUserResponse

    suspend fun handleDeleteOwnUserRequest(principle: JWTPrincipal): DeleteUserResponse

    suspend fun handleGetPagedAuthorsRequest(request: GetPagedAuthorsRequest): List<AuthorInfo>

    suspend fun handleGetPagedUsersRequest(request: GetPagedUserAdminRequest): List<User>

    suspend fun handleDeleteUserRequest(request: DeleteUserRequest): DeleteUserResponse

}