package com.quizappbackend.routing

import com.quizappbackend.authentication.JwtAuth
import com.quizappbackend.authentication.JwtAuth.userId
import com.quizappbackend.authentication.JwtAuth.userPrinciple
import com.quizappbackend.authentication.JwtAuth.userRole
import com.quizappbackend.authentication.UserCredentialsChangedException
import com.quizappbackend.authentication.UserCredentialsErrorType.*
import com.quizappbackend.model.databases.mongodb.documents.user.User
import com.quizappbackend.model.networking.requests.*
import com.quizappbackend.model.networking.responses.*
import com.quizappbackend.model.networking.responses.DeleteUserResponse.DeleteUserResponseType
import com.quizappbackend.model.networking.responses.LoginUserResponse.LoginUserResponseType
import com.quizappbackend.model.networking.responses.RegisterUserResponse.RegisterUserResponseType
import com.quizappbackend.model.networking.responses.SyncUserDataResponse.SyncUserDataResponseType
import com.quizappbackend.model.networking.responses.UpdateUserResponse.UpdateUserResponseType
import com.quizappbackend.model.databases.mongodb.documents.user.Role
import com.quizappbackend.model.networking.responses.ChangePasswordResponse.*
import com.quizappbackend.model.networking.responses.CreateUserResponse.*
import com.quizappbackend.mongoRepository
import com.quizappbackend.routing.ApiPaths.*
import com.quizappbackend.utils.generateHash
import com.quizappbackend.utils.matchesHash
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Routing.registerUserRoutes() {
    registerLoginRoute()
    registerRegisterRoute()
    refreshTokenRoute()
    registerUpdateUserNameRoute()
    registerDeleteOwnUserRoute()
    registerGetPagedUsersRoute()
    registerGetPagedUsersAdminRoute()
    registerChangePasswordRoute()
    registerUpdateUserRoleRoute()
    registerDeleteUsersRoute()
    registerSyncUserInfo()
    registerCreateUserRoute()
}

private fun Route.registerLoginRoute() = post(UserPaths.LOGIN) {
    val request = call.receive<LoginUserRequest>()

    mongoRepository.getUserWithUserName(request.userName)?.let { user ->
        if (request.password.matchesHash(user.password)) {
            call.respond(
                HttpStatusCode.OK, LoginUserResponse(
                    userId = user.id,
                    role = user.role,
                    lastModifiedTimeStamp = user.lastModifiedTimestamp,
                    token = JwtAuth.generateToken(user),
                    responseType = LoginUserResponseType.LOGIN_SUCCESSFUL
                )
            )
            return@post
        }
    }

    call.respond(HttpStatusCode.OK, LoginUserResponse(responseType = LoginUserResponseType.USER_NAME_OR_PASSWORD_WRONG))
}

private fun Route.registerRegisterRoute() = post(UserPaths.REGISTER) {
    val request = call.receive<RegisterUserRequest>()

    if (mongoRepository.checkIfUserExistsWithName(request.userName)) {
        call.respond(
            HttpStatusCode.OK,
            RegisterUserResponse(RegisterUserResponseType.USER_ALREADY_EXISTS)
        )
        return@post
    }


    mongoRepository.insertOne(User(userName = request.userName, password = request.password.generateHash(), role = Role.USER)).let { acknowledged ->
        call.respond(
            HttpStatusCode.OK,
            RegisterUserResponse(
                if (acknowledged) RegisterUserResponseType.REGISTER_SUCCESSFUL
                else RegisterUserResponseType.NOT_ACKNOWLEDGED
            )
        )
    }
}

private fun Route.refreshTokenRoute() = post(UserPaths.REFRESH_TOKEN) {
    val request = call.receive<RefreshJwtTokenRequest>()
    mongoRepository.getUserWithUserName(request.userName)?.let { user ->
        if (request.password.matchesHash(user.password)) {
            call.respond(HttpStatusCode.OK, RefreshJwtTokenResponse(JwtAuth.generateToken(user)))
            return@post
        }
    }
    throw UserCredentialsChangedException(CREDENTIALS_CHANGED)
}


private fun Route.registerUpdateUserNameRoute() = authenticate {
    post(UserPaths.UPDATE_USERNAME) {
        val request = call.receive<UpdateUserNameRequest>()

        if (mongoRepository.checkIfUserExistsWithName(request.newUserName)) {
            call.respond(
                HttpStatusCode.OK,
                UpdateUserResponse(UpdateUserResponseType.USERNAME_ALREADY_TAKEN)
            )
            return@post
        }

        mongoRepository.changeUserName(userPrinciple.userId, request.newUserName).let { wasAcknowledged ->
            call.respond(
                HttpStatusCode.OK,
                UpdateUserResponse(
                    if (wasAcknowledged) UpdateUserResponseType.UPDATE_SUCCESSFUL
                    else UpdateUserResponseType.NOT_ACKNOWLEDGED
                )
            )
        }
    }
}


private fun Route.registerDeleteOwnUserRoute() = authenticate {
    delete(UserPaths.DELETE_SELF) {
        mongoRepository.deleteUser(userPrinciple.userId).let { acknowledged ->
            call.respond(
                HttpStatusCode.OK,
                DeleteUserResponse(
                    if (acknowledged) DeleteUserResponseType.SUCCESSFUL
                    else DeleteUserResponseType.NOT_ACKNOWLEDGED
                )
            )
        }
    }
}

private fun Route.registerSyncUserInfo() = authenticate {
    post(UserPaths.SYNC) {
        val request = call.receive<SyncUserDataRequest>()

        mongoRepository.findOneById<User>(request.userId)?.let { user ->
            if (user.role == userPrinciple.userRole) {
                call.respond(HttpStatusCode.OK, SyncUserDataResponse(responseType = SyncUserDataResponseType.DATA_UP_TO_DATE))
                return@post
            }
            call.respond(
                HttpStatusCode.OK, SyncUserDataResponse(
                    role = user.role,
                    lastModifiedTimestamp = user.lastModifiedTimestamp,
                    responseType = SyncUserDataResponseType.DATA_CHANGED
                )
            )
        }
    }
}

private fun Route.registerGetPagedUsersRoute() = authenticate {
    post(UserPaths.AUTHORS_PAGED) {
        val request = call.receive<GetPagedAuthorsRequest>()

        mongoRepository.getAuthorsPaged(
            request.limit,
            request.page,
            request.searchString
        ).let { authors ->
            call.respond(HttpStatusCode.OK, authors)
        }
    }
}


private fun Route.registerChangePasswordRoute() = authenticate {
    post(UserPaths.CHANGE_PASSWORD) {
        val request = call.receive<ChangePasswordRequest>()

        mongoRepository.changeUserPassword(userPrinciple.userId, request.newPassword.generateHash()).let { wasAcknowledged ->
            if (wasAcknowledged) {
                mongoRepository.findOneById<User>(userPrinciple.userId)?.let { user ->
                    call.respond(
                        ChangePasswordResponse(
                            JwtAuth.generateToken(user),
                            ChangePasswordResponseType.SUCCESSFUL
                        )
                    )
                    return@post
                }
            }
        }

        call.respond(ChangePasswordResponse(responseType = ChangePasswordResponseType.NOT_ACKNOWLEDGED))
    }
}


//ADMIN ROUTES
private fun Route.registerGetPagedUsersAdminRoute() = authenticate(JwtAuth.ADMIN_ROUTE) {
    post(UserPaths.USERS_PAGED_ADMIN) {
        val request = call.receive<GetPagedUserAdminRequest>()

        mongoRepository.getUsersPaged(
            request.limit,
            request.page,
            request.searchString,
            request.roles,
            request.orderBy,
            request.ascending
        ).let { users ->
            call.respond(HttpStatusCode.OK, users)
        }
    }
}

private fun Route.registerUpdateUserRoleRoute() = authenticate(JwtAuth.ADMIN_ROUTE) {
    post(UserPaths.UPDATE_ROLE) {
        val request = call.receive<UpdateUserRoleRequest>()

        if (!mongoRepository.checkIfExistsWithId<User>(request.userId)) {
            call.respond(HttpStatusCode.OK, UpdateUserResponse(UpdateUserResponseType.USER_DOES_NOT_EXIST))
        } else {
            mongoRepository.updateUserRole(request.userId, request.newRole).let { wasAcknowledged ->
                call.respond(
                    HttpStatusCode.OK, UpdateUserResponse(
                        if (wasAcknowledged) UpdateUserResponseType.UPDATE_SUCCESSFUL
                        else UpdateUserResponseType.NOT_ACKNOWLEDGED
                    )
                )
            }
        }
    }
}


private fun Route.registerDeleteUsersRoute() = authenticate(JwtAuth.ADMIN_ROUTE) {
    delete(UserPaths.DELETE_USER) {
        val request = call.receive<DeleteUsersRequest>()

        mongoRepository.deleteUsers(request.userIds).let { acknowledged ->
            call.respond(
                HttpStatusCode.OK,
                DeleteUserResponse(
                    if (acknowledged) DeleteUserResponseType.SUCCESSFUL
                    else DeleteUserResponseType.NOT_ACKNOWLEDGED
                )
            )
        }
    }
}


private fun Route.registerCreateUserRoute() = authenticate(JwtAuth.ADMIN_ROUTE) {
    post(UserPaths.CREATE) {
        val request = call.receive<CreateUserRequest>()

        if (mongoRepository.checkIfUserExistsWithName(request.userName)) {
            call.respond(
                HttpStatusCode.OK,
                CreateUserResponse(CreateUserResponseType.USER_ALREADY_EXISTS)
            )
            return@post
        }


        mongoRepository.insertOne(User(userName = request.userName, password = request.password.generateHash(), role = request.role)).let { acknowledged ->
            call.respond(
                HttpStatusCode.OK,
                CreateUserResponse(
                    if (acknowledged) CreateUserResponseType.CREATION_SUCCESSFUL
                    else CreateUserResponseType.NOT_ACKNOWLEDGED
                )
            )
        }
    }
}