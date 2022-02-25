package com.quizappbackend.model.mongodb.dao

import com.quizappbackend.model.ktor.BackendRequest
import com.quizappbackend.model.mongodb.documents.User
import com.quizappbackend.model.mongodb.properties.AuthorInfo
import com.quizappbackend.model.mongodb.properties.Role

interface UserDao: BaseDao<User> {

    suspend fun findUserByName(userName: String): User?

    suspend fun updateUserRole(userId: String, role: Role): Boolean

    suspend fun updateUserPassword(userId: String, newPassword: String): Boolean

    suspend fun updateUserCanShareQuestionnaireWith(userId: String, canShare: Boolean): Boolean

    suspend fun getPagedUsers(request: BackendRequest.GetPagedUserAdminRequest): List<User>

    suspend fun getPagedAuthors(limit: Int, page: Int, searchQuery: String): List<AuthorInfo>

}