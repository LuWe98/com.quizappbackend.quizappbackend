package com.quizappbackend.model.mongodb.dao

import com.quizappbackend.model.mongodb.documents.User
import com.quizappbackend.model.mongodb.dto.ManageUsersOrderBy
import com.quizappbackend.model.mongodb.properties.AuthorInfo
import com.quizappbackend.model.mongodb.properties.Role

interface UserDao: BaseDao<User> {

    suspend fun findUserByName(userName: String): User?

    suspend fun updateUserName(userId: String, newUserName: String): Boolean

    suspend fun updateUserRole(userId: String, role: Role): Boolean

    suspend fun updateUserPassword(userId: String, newPassword: String): Boolean

    suspend fun getUsersPaged(limit: Int, page: Int, searchQuery: String, roles: Set<Role>, orderBy: ManageUsersOrderBy, ascending: Boolean): List<User>

    suspend fun getAuthorsPaged(limit: Int, page: Int, searchQuery: String): List<AuthorInfo>

}