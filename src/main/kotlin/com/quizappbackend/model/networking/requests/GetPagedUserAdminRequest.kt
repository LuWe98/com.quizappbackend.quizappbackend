package com.quizappbackend.model.networking.requests

import com.quizappbackend.model.databases.dto.ManageUsersOrderBy
import com.quizappbackend.model.databases.mongodb.documents.user.Role
import kotlinx.serialization.Serializable

@Serializable
data class GetPagedUserAdminRequest(
    val limit: Int,
    val page: Int,
    val searchString: String,
    val roles: Set<Role>,
    val orderBy: ManageUsersOrderBy,
    val ascending: Boolean
)