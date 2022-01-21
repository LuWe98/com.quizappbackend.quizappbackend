package com.quizappbackend.routing.services

import com.quizappbackend.authentication.*
import com.quizappbackend.authentication.JwtAuth.userId
import com.quizappbackend.authentication.JwtAuth.userRole
import com.quizappbackend.model.ktor.BackendRequest.*
import com.quizappbackend.model.ktor.BackendResponse.*
import com.quizappbackend.model.mongodb.MongoRepository
import com.quizappbackend.model.mongodb.documents.User
import com.quizappbackend.model.mongodb.properties.AuthorInfo
import com.quizappbackend.model.mongodb.properties.Role
import com.quizappbackend.utils.generateHash
import com.quizappbackend.utils.matchesHash
import io.ktor.auth.jwt.*

class UserRouteServiceImpl(
    private val mongoRepository: MongoRepository
) : UserRouteService {

    override suspend fun handleRegisterUserRequest(request: RegisterUserRequest): RegisterUserResponse {
        if (mongoRepository.checkIfUserExistsWithName(request.userName)) {
            return RegisterUserResponse(RegisterUserResponse.RegisterUserResponseType.USER_ALREADY_EXISTS)
        }

        mongoRepository.insertOne(User(userName = request.userName, password = request.password.generateHash(), role = Role.USER)).let { acknowledged ->
            return RegisterUserResponse(
                if (acknowledged) RegisterUserResponse.RegisterUserResponseType.REGISTER_SUCCESSFUL
                else RegisterUserResponse.RegisterUserResponseType.NOT_ACKNOWLEDGED
            )
        }
    }


    override suspend fun handleLoginUserRequest(request: LoginUserRequest): LoginUserResponse {
        mongoRepository.findUserByName(request.userName)?.let { user ->
            if (!request.password.matchesHash(user.password)) return@let
            return LoginUserResponse(
                userId = user.id,
                role = user.role,
                lastModifiedTimeStamp = user.lastModifiedTimestamp,
                token = JwtAuth.generateToken(user),
                responseType = LoginUserResponse.LoginUserResponseType.LOGIN_SUCCESSFUL
            )
        }

        return LoginUserResponse(responseType = LoginUserResponse.LoginUserResponseType.USER_NAME_OR_PASSWORD_WRONG)
    }


    override suspend fun handleCreateUserRequest(request: CreateUserRequest): CreateUserResponse {
        if (mongoRepository.checkIfUserExistsWithName(request.userName)) {
            return CreateUserResponse(CreateUserResponse.CreateUserResponseType.USER_ALREADY_EXISTS)
        }

        mongoRepository.insertOne(User(userName = request.userName, password = request.password.generateHash(), role = request.role)).let { acknowledged ->
            return CreateUserResponse(
                if (acknowledged) CreateUserResponse.CreateUserResponseType.CREATION_SUCCESSFUL
                else CreateUserResponse.CreateUserResponseType.NOT_ACKNOWLEDGED
            )
        }
    }


    override suspend fun handleGetRefreshTokenRequest(request: RefreshJwtTokenRequest): RefreshJwtTokenResponse {
        mongoRepository.findUserByName(request.userName)?.let { user ->
            if (!request.password.matchesHash(user.password)) return@let
            return RefreshJwtTokenResponse(JwtAuth.generateToken(user))
        }
        throw UserCredentialsChangedException(UserCredentialsErrorType.CREDENTIALS_CHANGED)
    }


    override suspend fun handleSyncUserDataRequest(principle: JWTPrincipal, request: SyncUserDataRequest): SyncUserDataResponse {
        mongoRepository.findOneById<User>(request.userId)?.let { user ->
            if (user.role == principle.userRole) {
                return SyncUserDataResponse(responseType = SyncUserDataResponse.SyncUserDataResponseType.DATA_UP_TO_DATE)
            }
            return SyncUserDataResponse(
                role = user.role,
                lastModifiedTimestamp = user.lastModifiedTimestamp,
                responseType = SyncUserDataResponse.SyncUserDataResponseType.DATA_CHANGED
            )
        }
        throw UserCredentialsChangedException(UserCredentialsErrorType.CREDENTIALS_CHANGED)
    }


    override suspend fun handleUpdateUserNameRequest(principle: JWTPrincipal, request: UpdateUserNameRequest): UpdateUserResponse {
        if (mongoRepository.checkIfUserExistsWithName(request.newUserName)) {
            return UpdateUserResponse(UpdateUserResponse.UpdateUserResponseType.USERNAME_ALREADY_TAKEN)
        }

        mongoRepository.updateUserName(principle.userId, request.newUserName).let { wasAcknowledged ->
            return UpdateUserResponse(
                if (wasAcknowledged) UpdateUserResponse.UpdateUserResponseType.UPDATE_SUCCESSFUL
                else UpdateUserResponse.UpdateUserResponseType.NOT_ACKNOWLEDGED
            )
        }
    }


    override suspend fun handleUpdatePasswordRequest(principle: JWTPrincipal, request: ChangePasswordRequest): ChangePasswordResponse {
        mongoRepository.updateUserPassword(principle.userId, request.newPassword.generateHash()).let { wasAcknowledged ->
            if (wasAcknowledged) {
                mongoRepository.findOneById<User>(principle.userId)?.let { user ->
                    return ChangePasswordResponse(
                        JwtAuth.generateToken(user),
                        ChangePasswordResponse.ChangePasswordResponseType.SUCCESSFUL
                    )

                }
            }
        }

        return ChangePasswordResponse(responseType = ChangePasswordResponse.ChangePasswordResponseType.NOT_ACKNOWLEDGED)
    }


    override suspend fun handleUpdateUserRoleRequest(request: UpdateUserRoleRequest): UpdateUserResponse {
        mongoRepository.findOneById<User>(request.userId)?.let {
            mongoRepository.updateUserRole(request.userId, request.newRole).let { wasAcknowledged ->
                return UpdateUserResponse(
                    if (wasAcknowledged) UpdateUserResponse.UpdateUserResponseType.UPDATE_SUCCESSFUL
                    else UpdateUserResponse.UpdateUserResponseType.NOT_ACKNOWLEDGED
                )
            }
        }

        return UpdateUserResponse(UpdateUserResponse.UpdateUserResponseType.USER_DOES_NOT_EXIST)
    }


    override suspend fun handleDeleteOwnUserRequest(principle: JWTPrincipal): DeleteUserResponse {
        mongoRepository.deleteUser(principle.userId).let { acknowledged ->
            return DeleteUserResponse(
                if (acknowledged) DeleteUserResponse.DeleteUserResponseType.SUCCESSFUL
                else DeleteUserResponse.DeleteUserResponseType.NOT_ACKNOWLEDGED
            )
        }
    }


    override suspend fun handleGetPagedAuthorsRequest(request: GetPagedAuthorsRequest): List<AuthorInfo> {
        return mongoRepository.getAuthorsPaged(
            request.limit,
            request.page,
            request.searchString
        )
    }


    override suspend fun handleGetPagedUsersRequest(request: GetPagedUserAdminRequest): List<User> {
        return mongoRepository.getUsersPaged(
            request.limit,
            request.page,
            request.searchString,
            request.roles,
            request.orderBy,
            request.ascending
        )
    }


    override suspend fun handleDeleteUserRequest(request: DeleteUserRequest): DeleteUserResponse {
        mongoRepository.deleteUser(request.userId).let { acknowledged ->
            return DeleteUserResponse(
                if (acknowledged) DeleteUserResponse.DeleteUserResponseType.SUCCESSFUL
                else DeleteUserResponse.DeleteUserResponseType.NOT_ACKNOWLEDGED
            )
        }
    }
}