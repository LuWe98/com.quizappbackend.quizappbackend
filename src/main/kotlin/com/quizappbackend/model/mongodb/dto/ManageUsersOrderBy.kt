package com.quizappbackend.model.mongodb.dto

import com.quizappbackend.model.mongodb.documents.User
import kotlin.reflect.KProperty1

enum class ManageUsersOrderBy(val orderByProperty: KProperty1<User, Any>) {
    USER_NAME(User::userName),
    ROLE(User::role)
}