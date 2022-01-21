package com.quizappbackend.routing.services

import com.quizappbackend.model.ktor.BackendRequest
import com.quizappbackend.model.ktor.BackendResponse

interface CourseOfStudiesRouteService {

    suspend fun handleSyncRequest(request: BackendRequest.SyncCoursesOfStudiesRequest): BackendResponse.SyncCoursesOfStudiesResponse

    suspend fun handleInsertRequest(request: BackendRequest.InsertCourseOfStudiesRequest): BackendResponse.InsertCourseOfStudiesResponse

    suspend fun handleDeleteRequest(request: BackendRequest.DeleteCourseOfStudiesRequest): BackendResponse.DeleteCourseOfStudiesResponse

}