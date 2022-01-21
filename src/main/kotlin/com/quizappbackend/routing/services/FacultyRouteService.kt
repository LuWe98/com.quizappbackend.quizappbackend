package com.quizappbackend.routing.services

import com.quizappbackend.model.ktor.BackendRequest
import com.quizappbackend.model.ktor.BackendResponse

interface FacultyRouteService {

    suspend fun handleSyncRequest(request: BackendRequest.SyncFacultiesRequest): BackendResponse.SyncFacultiesResponse

    suspend fun handleInsertRequest(request: BackendRequest.InsertFacultyRequest): BackendResponse.InsertFacultyResponse

    suspend fun handleDeleteRequest(request: BackendRequest.DeleteFacultyRequest): BackendResponse.DeleteFacultyResponse

}