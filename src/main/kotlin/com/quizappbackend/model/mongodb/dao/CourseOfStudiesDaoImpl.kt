package com.quizappbackend.model.mongodb.dao

import com.mongodb.client.model.UpdateManyModel
import com.quizappbackend.model.mongodb.documents.MongoCourseOfStudies
import com.quizappbackend.model.mongodb.dto.CourseOfStudiesIdWithTimeStamp
import io.ktor.util.date.*
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.CoroutineCollection

class CourseOfStudiesDaoImpl(
    override var collection: CoroutineCollection<MongoCourseOfStudies>
) :CourseOfStudiesDao {

    override suspend fun isCourseOfStudiesAbbreviationAlreadyUsed(courseOfStudies: MongoCourseOfStudies) =
        collection.findOne(and(MongoCourseOfStudies::abbreviation eq courseOfStudies.abbreviation, MongoCourseOfStudies::id ne courseOfStudies.id)) != null

    override suspend fun findCourseOfStudiesByFacultyId(facultyId: String) = collection.find(MongoCourseOfStudies::facultyIds contains facultyId).toList()


    override suspend fun findCourseOfStudiesToDeleteLocally(localCourseOfStudiesIdsWithTimeStamp: List<CourseOfStudiesIdWithTimeStamp>): List<String> {
        return localCourseOfStudiesIdsWithTimeStamp.map(CourseOfStudiesIdWithTimeStamp::courseOfStudiesId).let { facultyIds ->
            collection.find(MongoCourseOfStudies::id `in` facultyIds).toList().let { foundFaculties ->
                facultyIds.filter { id -> foundFaculties.none { it.id == id } }
            }
        }
    }

    override suspend fun findCourseOfStudiesNotUpToDateOfUser(localCourseOfStudiesIdsWithTimeStamp: List<CourseOfStudiesIdWithTimeStamp>): List<MongoCourseOfStudies> {
        return localCourseOfStudiesIdsWithTimeStamp.map(CourseOfStudiesIdWithTimeStamp::courseOfStudiesId).let { facultyIds ->
            localCourseOfStudiesIdsWithTimeStamp.map(::mapFacultyIdsToAreNotEqualToTimestamp).let { filters ->
                collection.find(
                    or(
                        or(filters),
                        MongoCourseOfStudies::id nin facultyIds
                    )
                ).toList()
            }
        }
    }

    override suspend fun removeFacultyFromCourseOfStudies(facultyId: String): Boolean {
        val updateTimestamp = UpdateManyModel<MongoCourseOfStudies>(
            MongoCourseOfStudies::facultyIds contains facultyId,
            set(MongoCourseOfStudies::lastModifiedTimestamp setTo getTimeMillis())
        )

        val removeFacultyId = UpdateManyModel<MongoCourseOfStudies>(
            MongoCourseOfStudies::facultyIds contains facultyId,
            pull(MongoCourseOfStudies::facultyIds, facultyId)
        )

        return collection.bulkWrite(updateTimestamp, removeFacultyId).wasAcknowledged()
    }

    private fun mapFacultyIdsToAreNotEqualToTimestamp(courseOfStudiesIdWithTimeStamp: CourseOfStudiesIdWithTimeStamp) = and(
        MongoCourseOfStudies::id eq courseOfStudiesIdWithTimeStamp.courseOfStudiesId,
        MongoCourseOfStudies::lastModifiedTimestamp ne courseOfStudiesIdWithTimeStamp.lastModifiedTimestamp
    )


//    suspend fun generateSyncFacultiesResponse(courseOfStudiesIdsWithTimeStamps: List<CourseOfStudiesIdWithTimeStamp>) : SyncCoursesOfStudiesResponse = withContext(IO) {
//        val localCourseOfStudiesIds = courseOfStudiesIdsWithTimeStamps.map(CourseOfStudiesIdWithTimeStamp::courseOfStudiesId)
//
//        val coursesOfStudiesToDownloadAsync = async {
//            collection.find(or(buildFilter(courseOfStudiesIdsWithTimeStamps), MongoCourseOfStudies::id nin localCourseOfStudiesIds)).toList()
//        }
//
//        val coursesOfStudiesToDeleteAsync = async {
//            collection.find(MongoCourseOfStudies::id `in` localCourseOfStudiesIds).toList().let { allCoursesOfStudies ->
//                localCourseOfStudiesIds.filter { id -> allCoursesOfStudies.none { it.id == id } }
//            }
//        }
//
//        val coursesOfStudiesToUpdate: List<MongoCourseOfStudies>
//        val coursesOfStudiesToInsert: List<MongoCourseOfStudies>
//
//        coursesOfStudiesToDownloadAsync.await().let {
//            coursesOfStudiesToUpdate = it.filter { cos -> localCourseOfStudiesIds.any { id -> cos.id == id } }
//            coursesOfStudiesToInsert = it - coursesOfStudiesToUpdate.toSet()
//        }
//
//        SyncCoursesOfStudiesResponse(
//            coursesOfStudiesToInsert = coursesOfStudiesToInsert,
//            coursesOfStudiesToUpdate = coursesOfStudiesToUpdate,
//            courseOfStudiesIdsToDelete = coursesOfStudiesToDeleteAsync.await()
//        )
//    }
//
//    private fun buildFilter(courseOfStudiesIdsWithTimeStamps: List<CourseOfStudiesIdWithTimeStamp>) =
//        or(courseOfStudiesIdsWithTimeStamps.map {
//            and(MongoCourseOfStudies::id eq it.courseOfStudiesId, MongoCourseOfStudies::lastModifiedTimestamp ne it.lastModifiedTimestamp)
//        })

}