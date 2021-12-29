package com.quizappbackend.authentication

class UserUnauthorizedException(val schema: String, val realm: String) : IllegalArgumentException()