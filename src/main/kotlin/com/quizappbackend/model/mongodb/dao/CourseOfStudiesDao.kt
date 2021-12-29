package com.quizappbackend.model.mongodb.dao

import com.quizappbackend.model.mongodb.dto.CourseOfStudiesIdWithTimeStamp
import com.quizappbackend.model.mongodb.documents.MongoCourseOfStudies
import com.quizappbackend.model.ktor.BackendResponse.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.CoroutineCollection

class CourseOfStudiesDao(override var collection: CoroutineCollection<MongoCourseOfStudies>) : BaseDao<MongoCourseOfStudies>(collection) {

    suspend fun isCourseOfStudiesAbbreviationAlreadyUsed(courseOfStudies: MongoCourseOfStudies) =
        collection.findOne(and(MongoCourseOfStudies::abbreviation eq courseOfStudies.abbreviation, MongoCourseOfStudies::id ne courseOfStudies.id)) != null

    suspend fun getCourseOfStudiesForFaculty(facultyId: String) = collection.find(MongoCourseOfStudies::facultyIds contains facultyId).toList()

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