package com.quizappbackend.routing.services

import com.quizappbackend.model.ktor.BackendRequest.*
import com.quizappbackend.model.ktor.BackendResponse.*
import com.quizappbackend.model.mongodb.MongoRepository
import com.quizappbackend.model.mongodb.documents.MongoFaculty
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class FacultyRouteServiceImpl(
    private val mongoRepository: MongoRepository
) : FacultyRouteService {

    // TODO -> Das hier noch machen, geht noch nicht
    override suspend fun handleSyncRequest(request: SyncFacultiesRequest): SyncFacultiesResponse = withContext(IO) {

        val facultiesNotUpToDateAsync = async { mongoRepository.findFacultiesNotUpToDateOfUser(request.localFacultyIdsWithTimeStamp) }

        val facultiesIdsToDeleteAsync = async { mongoRepository.findFacultiesToDeleteLocally(request.localFacultyIdsWithTimeStamp) }

        val facultiesToUpdate: List<MongoFaculty>
        val facultiesToInsert: List<MongoFaculty>

        facultiesNotUpToDateAsync.await().let { notUpToDateFaculties ->
            facultiesToUpdate = notUpToDateFaculties.filter { faculty ->
                request.localFacultyIdsWithTimeStamp.any { faculty.id == it.facultyId }
            }
            facultiesToInsert = notUpToDateFaculties - facultiesToUpdate.toSet()
        }

        return@withContext SyncFacultiesResponse(
            facultiesToInsert = facultiesToInsert,
            facultiesToUpdate = facultiesToUpdate,
            facultyIdsToDelete = facultiesIdsToDeleteAsync.await()
        )
    }

    override suspend fun handleInsertRequest(request: InsertFacultyRequest): InsertFacultyResponse {

        if (mongoRepository.isFacultyAbbreviationAlreadyUsed(request.faculty)) {
            return InsertFacultyResponse(InsertFacultyResponse.InsertFacultyResponseType.ABBREVIATION_ALREADY_USED)
        }

        if (mongoRepository.isFacultyNameAlreadyUsed(request.faculty)) {
            return InsertFacultyResponse(InsertFacultyResponse.InsertFacultyResponseType.NAME_ALREADY_USED)
        }

        mongoRepository.insertOrReplaceFaculty(request.faculty).let { wasAcknowledged ->
            return InsertFacultyResponse(
                if (wasAcknowledged) InsertFacultyResponse.InsertFacultyResponseType.SUCCESSFUL
                else InsertFacultyResponse.InsertFacultyResponseType.NOT_ACKNOWLEDGED
            )
        }
    }

    override suspend fun handleDeleteRequest(request: DeleteFacultyRequest): DeleteFacultyResponse {
        mongoRepository.deleteFacultyById(request.facultyId).let { wasAcknowledged ->
            return DeleteFacultyResponse(
                if (wasAcknowledged) DeleteFacultyResponse.DeleteFacultyResponseType.SUCCESSFUL
                else DeleteFacultyResponse.DeleteFacultyResponseType.NOT_ACKNOWLEDGED
            )
        }
    }
}