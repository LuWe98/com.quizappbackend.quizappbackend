package com.quizappbackend.routing

import com.quizappbackend.authentication.JwtAuth
import com.quizappbackend.model.ktor.BackendRequest.*
import com.quizappbackend.model.ktor.BackendResponse.DeleteFacultyResponse
import com.quizappbackend.model.ktor.BackendResponse.DeleteFacultyResponse.DeleteFacultyResponseType
import com.quizappbackend.model.ktor.BackendResponse.InsertFacultyResponse
import com.quizappbackend.model.ktor.BackendResponse.InsertFacultyResponse.InsertFacultyResponseType
import com.quizappbackend.mongoRepository
import com.quizappbackend.routing.ApiPaths.FacultyPaths
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Routing.registerFacultyRoutes() {
    registerSyncFacultiesRoute()
    registerInsertFacultyRoute()
    registerDeleteFacultyRoute()
}

private fun Routing.registerSyncFacultiesRoute() = authenticate {
    post(FacultyPaths.SYNC) {
        call.receive<SyncFacultiesRequest>().let { request ->
            mongoRepository.generateSyncFacultiesResponse(request.localFacultyIdsWithTimeStamp).let { response ->
                call.respond(HttpStatusCode.OK, response)
            }
        }
    }
}

private fun Routing.registerInsertFacultyRoute() = authenticate(JwtAuth.ADMIN_ROUTE) {
    post(FacultyPaths.INSERT) {
        val request = call.receive<InsertFacultyRequest>()

        mongoRepository.isFacultyAbbreviationAlreadyUsed(request.faculty).let { isUsed ->
            if(isUsed) {
                call.respond(InsertFacultyResponse(InsertFacultyResponseType.ABBREVIATION_ALREADY_USED))
                return@post
            }
        }

        mongoRepository.isFacultyNameAlreadyUsed(request.faculty).let { isUsed ->
            if(isUsed){
                call.respond(InsertFacultyResponse(InsertFacultyResponseType.NAME_ALREADY_USED))
                return@post
            }
        }

        mongoRepository.insertOrReplaceFaculty(request.faculty).let { wasAcknowledged ->
            call.respond(
                InsertFacultyResponse(
                    if (wasAcknowledged) InsertFacultyResponseType.SUCCESSFUL
                    else InsertFacultyResponseType.NOT_ACKNOWLEDGED
                )
            )
        }
    }
}

private fun Routing.registerDeleteFacultyRoute() = authenticate(JwtAuth.ADMIN_ROUTE) {
    post(FacultyPaths.DELETE) {
        val request = call.receive<DeleteFacultyRequest>()

        mongoRepository.deleteFacultyById(request.facultyId).let { wasAcknowledged ->
            call.respond(
                DeleteFacultyResponse(
                    if (wasAcknowledged) DeleteFacultyResponseType.SUCCESSFUL
                    else DeleteFacultyResponseType.NOT_ACKNOWLEDGED
                )
            )
        }
    }
}