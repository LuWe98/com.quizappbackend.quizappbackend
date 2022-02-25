package com.quizappbackend.model.mongodb.properties

enum class Role {
    ADMIN,
    CREATOR,
    USER;

    fun hasPrivilege(roleToCheck: Role) = when(this) {
        ADMIN -> true
        CREATOR -> roleToCheck == CREATOR || roleToCheck == USER
        USER -> roleToCheck == USER
    }

    companion object {
        val CREATOR_ROLES = setOf(ADMIN, CREATOR)
    }
}