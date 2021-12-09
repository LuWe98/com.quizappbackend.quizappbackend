package com.quizappbackend.routing

import com.quizappbackend.authentication.JwtAuth
import com.quizappbackend.model.databases.mongodb.documents.faculty.MongoCourseOfStudies
import com.quizappbackend.model.databases.mongodb.documents.faculty.MongoFaculty
import com.quizappbackend.model.networking.requests.DeleteCourseOfStudiesRequest
import com.quizappbackend.model.networking.requests.InsertCourseOfStudiesRequest
import com.quizappbackend.model.networking.requests.SyncCoursesOfStudiesRequest
import com.quizappbackend.model.networking.responses.DeleteCourseOfStudiesResponse
import com.quizappbackend.model.networking.responses.DeleteCourseOfStudiesResponse.DeleteCourseOfStudiesResponseType
import com.quizappbackend.model.networking.responses.InsertCourseOfStudiesResponse
import com.quizappbackend.model.networking.responses.InsertCourseOfStudiesResponse.InsertCourseOfStudiesResponseType
import com.quizappbackend.model.networking.responses.InsertFacultyResponse
import com.quizappbackend.mongoRepository
import com.quizappbackend.routing.ApiPaths.*
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Routing.registerCourseOfStudiesRoutes() {
    registerSyncCoursesOfStudiesRoute()
    registerInsertCourseOfStudiesRoute()
    registerDeleteCourseOfStudiesRoute()
}

private fun Routing.registerSyncCoursesOfStudiesRoute() = authenticate {
    post(CourseOfStudiesPaths.SYNC) {
        call.receive<SyncCoursesOfStudiesRequest>().let { request ->
            mongoRepository.generateSyncCoursesOfStudiesResponse(request.localCourseOfStudiesWithTimeStamp).let { response ->
                call.respond(HttpStatusCode.OK, response)
            }
        }
    }
}

private fun Routing.registerInsertCourseOfStudiesRoute() = authenticate(JwtAuth.ADMIN_ROUTE) {
    post(CourseOfStudiesPaths.INSERT) {
        val request = call.receive<InsertCourseOfStudiesRequest>()

        mongoRepository.isCourseOfStudiesAbbreviationAlreadyUsed(request.courseOfStudies).let { isUsed ->
            if(isUsed) {
                call.respond(InsertCourseOfStudiesResponse(InsertCourseOfStudiesResponseType.ABBREVIATION_ALREADY_USED))
                return@post
            }
        }

        mongoRepository.insertOrReplaceCourseOfStudies(request.courseOfStudies).let { wasAcknowledged ->
            call.respond(
                InsertCourseOfStudiesResponse(
                    if (wasAcknowledged) InsertCourseOfStudiesResponseType.SUCCESSFUL
                    else InsertCourseOfStudiesResponseType.NOT_ACKNOWLEDGED
                )
            )
        }
    }
}

private fun Routing.registerDeleteCourseOfStudiesRoute() = authenticate(JwtAuth.ADMIN_ROUTE) {
    post(CourseOfStudiesPaths.DELETE) {
        val request = call.receive<DeleteCourseOfStudiesRequest>()

        mongoRepository.deleteOneById<MongoCourseOfStudies>(request.courseOfStudiesId).let { wasAcknowledged ->
            call.respond(
                DeleteCourseOfStudiesResponse(
                    if (wasAcknowledged) DeleteCourseOfStudiesResponseType.SUCCESSFUL
                    else DeleteCourseOfStudiesResponseType.NOT_ACKNOWLEDGED
                )
            )
        }
    }
}