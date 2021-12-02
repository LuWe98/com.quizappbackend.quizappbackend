package com.quizappbackend.utils

import com.quizappbackend.authentication.JwtAuth.userName
import com.quizappbackend.authentication.JwtAuth.userPrinciple
import io.ktor.application.*
import io.ktor.request.*
import io.ktor.util.pipeline.*

fun PipelineContext<*, ApplicationCall>.logRequest() {
    call.application.environment.log.info("${context.request.httpMethod.value} - ${context.request.uri} - USER: ${userPrinciple.userName}")
}

fun PipelineContext<*, ApplicationCall>.log(message : String) {
    call.application.environment.log.debug(message)
}

fun PipelineContext<*, ApplicationCall>.logError(throwable: Throwable) {
    call.application.environment.log.error("TYPE: ${throwable.javaClass} - MESSAGE: ${throwable.message} - CAUSE: ${throwable.cause}\nSTACKTRACE: ${throwable.stackTraceToString()}")
}