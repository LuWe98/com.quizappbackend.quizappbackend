package com.quizappbackend.model.databases.dto

import com.quizappbackend.model.databases.mongodb.documents.user.User
import kotlin.reflect.KProperty1

enum class ManageUsersOrderBy(val orderByProperty: KProperty1<User, Any>) {
    USER_NAME(User::userName),
    ROLE(User::role)
}