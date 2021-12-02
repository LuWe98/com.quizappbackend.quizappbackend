package com.quizappbackend.authentication

/**
 * Users password changed and locally logged-in User has to be logged out
 */
class UserCredentialsChangedException(val userInfoError: UserCredentialsErrorType) : IllegalArgumentException()
