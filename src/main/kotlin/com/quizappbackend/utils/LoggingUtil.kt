package com.quizappbackend.utils

import io.ktor.application.*
import io.ktor.util.pipeline.*

fun PipelineContext<*, ApplicationCall>.log(message : String) {
    call.application.environment.log.debug(message)
}

fun PipelineContext<*, ApplicationCall>.logError(throwable: Throwable) {
    call.application.environment.log.error("ERROR TYPE: ${throwable.javaClass} - MESSAGE: ${throwable.message} - CAUSE: ${throwable.cause}\nSTACKTRACE: ${throwable.stackTraceToString()}")
}