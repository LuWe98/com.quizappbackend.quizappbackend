package com.quizappbackend.model.mongodb.dao

import com.quizappbackend.model.ktor.BackendRequest
import com.quizappbackend.model.mongodb.documents.User
import com.quizappbackend.model.mongodb.properties.AuthorInfo
import com.quizappbackend.model.mongodb.properties.Role
import io.ktor.util.date.*
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.projection
import org.litote.kmongo.coroutine.updateOne

class UserDaoImpl(override var collection: CoroutineCollection<User>) : UserDao {

    override suspend fun findUserByName(userName: String) = collection.findOne(User::name eq userName)

    override suspend fun updateUserRole(userId: String, role: Role) = collection.updateOne(
        User::id eq userId, set(User::role setTo role)
    ).wasAcknowledged()

    override suspend fun updateUserPassword(userId: String, newPassword: String) = collection.updateOne(
        User::id eq userId, set(User::password setTo newPassword, User::lastModifiedTimestamp setTo getTimeMillis())
    ).wasAcknowledged()

    override suspend fun updateUserCanShareQuestionnaireWith(userId: String, canShare: Boolean) = collection.updateOne(
        User::id eq userId, set(User::canShareQuestionnairesWith setTo canShare)
    ).wasAcknowledged()

    override suspend fun getPagedUsers(request: BackendRequest.GetPagedUserAdminRequest): List<User> = collection
        .find(and(User::name regex request.searchString, User::role `in` request.roles))
        .projection(exclude(User::password))
        .sort(orderBy(request.orderBy.orderByProperty, ascending = request.ascending))
        .skip((request.page - 1) * request.limit)
        .limit(request.limit)
        .toList()

    override suspend fun getPagedAuthors(limit: Int, page: Int, searchQuery: String) = collection
        .projection(User::id, User::name, and(User::name regex searchQuery, User::role `in` Role.CREATOR_ROLES))
        .sort(orderBy(User::name, ascending = true))
        .skip((page - 1) * limit)
        .limit(limit)
        .toList()
        .map { AuthorInfo(it.first!!, it.second!!) }

}