package com.quizappbackend.model.databases.mongodb.dao

import com.quizappbackend.model.databases.dto.ManageUsersOrderBy
import com.quizappbackend.model.databases.mongodb.documents.user.Role
import com.quizappbackend.model.databases.mongodb.documents.user.User
import io.ktor.util.date.*
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.CoroutineCollection

class UserDao(override var collection: CoroutineCollection<User>) : BaseDao<User>(collection) {

    suspend fun checkIfUserExistsWithName(userName: String) = collection.findOne(User::userName eq userName) != null

    suspend fun getUserWithUserName(userName: String) = collection.findOne(User::userName eq userName)

    suspend fun getUsersPaged(limit: Int, page: Int, searchQuery: String, roles: Set<Role>, orderBy: ManageUsersOrderBy, ascending: Boolean): List<User> =
        collection
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

    suspend fun getAuthorsPaged(limit: Int, page: Int, searchQuery: String) = collection
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

    suspend fun changeUserName(userId: String, newUserName: String) =
        collection.updateOne(User::id eq userId, set(User::userName setTo newUserName, User::lastModifiedTimestamp setTo getTimeMillis())).wasAcknowledged()

    suspend fun updateUserRole(userId: String, role: Role) =
        collection.updateOne(User::id eq userId, set(User::role setTo role, User::lastModifiedTimestamp setTo getTimeMillis())).wasAcknowledged()

    suspend fun changeUserPassword(userId: String, newPassword: String) =
        collection.updateOne(User::id eq userId, set(User::password setTo newPassword, User::lastModifiedTimestamp setTo getTimeMillis())).wasAcknowledged()

}