package com.quizappbackend.model.databases.mongodb.dao

import com.quizappbackend.model.databases.dto.CourseOfStudiesIdWithTimeStamp
import com.quizappbackend.model.databases.dto.FacultyIdWithTimeStamp
import com.quizappbackend.model.databases.mongodb.documents.faculty.MongoCourseOfStudies
import com.quizappbackend.model.databases.mongodb.documents.faculty.MongoFaculty
import com.quizappbackend.model.networking.responses.SyncCoursesOfStudiesResponse
import com.quizappbackend.model.networking.responses.SyncFacultiesResponse
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.CoroutineCollection

class CourseOfStudiesDao(override var collection: CoroutineCollection<MongoCourseOfStudies>) : BaseDao<MongoCourseOfStudies>(collection) {

    suspend fun isCourseOfStudiesAbbreviationAlreadyUsed(courseOfStudies: MongoCourseOfStudies) =
        collection.findOne(and(MongoCourseOfStudies::abbreviation eq courseOfStudies.abbreviation, MongoCourseOfStudies::id ne courseOfStudies.id)) != null

    suspend fun prefillCourseOfStudies(coursesOfStudies: List<MongoCourseOfStudies>) {
        if(collection.estimatedDocumentCount() != 0L) return
        insertMany(coursesOfStudies)
    }

    suspend fun generateSyncFacultiesResponse(courseOfStudiesIdsWithTimeStamps: List<CourseOfStudiesIdWithTimeStamp>) : SyncCoursesOfStudiesResponse = withContext(IO) {
        val localCourseOfStudiesIds = courseOfStudiesIdsWithTimeStamps.map(CourseOfStudiesIdWithTimeStamp::courseOfStudiesId)

        val coursesOfStudiesToDownloadAsync = async {
            collection.find(or(buildFilter(courseOfStudiesIdsWithTimeStamps), MongoCourseOfStudies::id nin localCourseOfStudiesIds)).toList()
        }

        val coursesOfStudiesToDeleteAsync = async {
            collection.find(MongoCourseOfStudies::id `in` localCourseOfStudiesIds).toList().let { allCoursesOfStudies ->
                localCourseOfStudiesIds.filter { id -> allCoursesOfStudies.none { it.id == id } }
            }
        }

        val coursesOfStudiesToUpdate: List<MongoCourseOfStudies>
        val coursesOfStudiesToInsert: List<MongoCourseOfStudies>

        coursesOfStudiesToDownloadAsync.await().let {
            coursesOfStudiesToUpdate = it.filter { cos -> localCourseOfStudiesIds.any { id -> cos.id == id } }
            coursesOfStudiesToInsert = it - coursesOfStudiesToUpdate.toSet()
        }

        SyncCoursesOfStudiesResponse(
            coursesOfStudiesToInsert = coursesOfStudiesToInsert,
            coursesOfStudiesToUpdate = coursesOfStudiesToUpdate,
            courseOfStudiesIdsToDelete = coursesOfStudiesToDeleteAsync.await()
        )
    }

    private fun buildFilter(courseOfStudiesIdsWithTimeStamps: List<CourseOfStudiesIdWithTimeStamp>) =
        or(courseOfStudiesIdsWithTimeStamps.map {
            and(MongoCourseOfStudies::id eq it.courseOfStudiesId, MongoCourseOfStudies::lastModifiedTimestamp ne it.lastModifiedTimestamp)
        })

}