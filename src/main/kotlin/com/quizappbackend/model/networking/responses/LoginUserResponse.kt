package com.quizappbackend.model.networking.responses

import com.quizappbackend.model.databases.mongodb.documents.user.Role
import kotlinx.serialization.Serializable

@Serializable
data class LoginUserResponse(
    val userId: String? = null,
    val role: Role? = null,
    val lastModifiedTimeStamp: Long? = null,
    val token: String? = null,
    val responseType: LoginUserResponseType,
) {
    enum class LoginUserResponseType {
        LOGIN_SUCCESSFUL,
        USER_NAME_OR_PASSWORD_WRONG
    }
}
