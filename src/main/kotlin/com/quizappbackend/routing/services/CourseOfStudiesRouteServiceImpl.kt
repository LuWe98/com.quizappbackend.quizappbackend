package com.quizappbackend.routing.services

import com.quizappbackend.model.ktor.BackendRequest.*
import com.quizappbackend.model.ktor.BackendResponse.*
import com.quizappbackend.model.mongodb.MongoRepository
import com.quizappbackend.model.mongodb.documents.MongoCourseOfStudies
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class CourseOfStudiesRouteServiceImpl(
    private val mongoRepository: MongoRepository
) : CourseOfStudiesRouteService {

    override suspend fun handleSyncRequest(request: SyncCoursesOfStudiesRequest): SyncCoursesOfStudiesResponse = withContext(IO) {

        val courseOfStudiesNotUpToDateAsync = async {
            mongoRepository.findCoursesOfStudiesNotUpToDateOfUser(request.localCourseOfStudiesWithTimeStamp)
        }

        val courseOfStudiesIdsToDeleteAsync = async {
            mongoRepository.findCourseOfStudiesIdsToDeleteLocally(request.localCourseOfStudiesWithTimeStamp)
        }

        val coursesOfStudiesToUpdate: List<MongoCourseOfStudies>
        val coursesOfStudiesToInsert: List<MongoCourseOfStudies>

        courseOfStudiesNotUpToDateAsync.await().let { notUpToDateCos ->
            coursesOfStudiesToUpdate = notUpToDateCos.filter { cos ->
                request.localCourseOfStudiesWithTimeStamp.any { cos.id == it.courseOfStudiesId }
            }
            coursesOfStudiesToInsert = notUpToDateCos - coursesOfStudiesToUpdate.toSet()
        }

        return@withContext SyncCoursesOfStudiesResponse(
            coursesOfStudiesToInsert = coursesOfStudiesToInsert,
            coursesOfStudiesToUpdate = coursesOfStudiesToUpdate,
            courseOfStudiesIdsToDelete = courseOfStudiesIdsToDeleteAsync.await()
        )
    }

    override suspend fun handleInsertRequest(request: InsertCourseOfStudiesRequest): InsertCourseOfStudiesResponse {
        mongoRepository.isCourseOfStudiesAbbreviationAlreadyUsed(request.courseOfStudies).let { isUsed ->
            if (isUsed) {
                return InsertCourseOfStudiesResponse(InsertCourseOfStudiesResponse.InsertCourseOfStudiesResponseType.ABBREVIATION_ALREADY_USED)
            }
        }

        mongoRepository.insertOrReplaceCourseOfStudies(request.courseOfStudies).let { wasAcknowledged ->
            return InsertCourseOfStudiesResponse(
                if (wasAcknowledged) InsertCourseOfStudiesResponse.InsertCourseOfStudiesResponseType.SUCCESSFUL
                else InsertCourseOfStudiesResponse.InsertCourseOfStudiesResponseType.NOT_ACKNOWLEDGED
            )
        }
    }

    override suspend fun handleDeleteRequest(request: DeleteCourseOfStudiesRequest): DeleteCourseOfStudiesResponse {
        mongoRepository.deleteCourseOfStudiesById(request.courseOfStudiesId).let { wasAcknowledged ->
            return DeleteCourseOfStudiesResponse(
                if (wasAcknowledged) DeleteCourseOfStudiesResponse.DeleteCourseOfStudiesResponseType.SUCCESSFUL
                else DeleteCourseOfStudiesResponse.DeleteCourseOfStudiesResponseType.NOT_ACKNOWLEDGED
            )
        }
    }
}