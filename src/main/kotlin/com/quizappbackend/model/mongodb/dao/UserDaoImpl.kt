package com.quizappbackend.model.mongodb.dao

import com.quizappbackend.model.mongodb.dto.ManageUsersOrderBy
import com.quizappbackend.model.mongodb.documents.User
import com.quizappbackend.model.mongodb.properties.Role
import io.ktor.util.date.*
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.CoroutineCollection

class UserDaoImpl(override var collection: CoroutineCollection<User>) : UserDao {

    override suspend fun findUserByName(userName: String) = collection.findOne(User::userName eq userName)

    override suspend fun updateUserName(userId: String, newUserName: String) = collection.updateOne(
        User::id eq userId, set(User::userName setTo newUserName, User::lastModifiedTimestamp setTo getTimeMillis())
    ).wasAcknowledged()

    override suspend fun updateUserRole(userId: String, role: Role) = collection.updateOne(
        User::id eq userId, set(User::role setTo role, User::lastModifiedTimestamp setTo getTimeMillis())
    ).wasAcknowledged()

    override suspend fun updateUserPassword(userId: String, newPassword: String) = collection.updateOne(
        User::id eq userId, set(User::password setTo newPassword, User::lastModifiedTimestamp setTo getTimeMillis())
    ).wasAcknowledged()

    override suspend fun getUsersPaged(
        limit: Int,
        page: Int,
        searchQuery: String,
        roles: Set<Role>,
        orderBy: ManageUsersOrderBy,
        ascending: Boolean
    ): List<User> = collection
        .find(
            and(
                User::userName regex searchQuery,
                User::role `in` roles
            )
        )
        .projection(exclude(User::password))
        .sort(orderBy(orderBy.orderByProperty, ascending = ascending))
        .skip((page - 1) * limit)
        .limit(limit)
        .toList()

    override suspend fun getAuthorsPaged(limit: Int, page: Int, searchQuery: String) = collection
        .find(
            and(
                User::userName regex searchQuery,
                User::role `in` setOf(Role.CREATOR, Role.ADMIN)
            )
        )
        .projection(fields(include(User::id, User::userName)))
        .sort(orderBy(User::userName, ascending = true))
        .skip((page - 1) * limit)
        .limit(limit)
        .toList()
        .map(User::asAuthorInfo)

}