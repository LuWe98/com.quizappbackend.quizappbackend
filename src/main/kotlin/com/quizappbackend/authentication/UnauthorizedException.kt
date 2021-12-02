package com.quizappbackend.authentication

class UnauthorizedException(val schema: String, val realm: String) : IllegalArgumentException()