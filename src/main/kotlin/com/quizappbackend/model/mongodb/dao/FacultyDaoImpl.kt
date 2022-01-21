package com.quizappbackend.model.mongodb.dao

import com.quizappbackend.model.mongodb.documents.MongoFaculty
import com.quizappbackend.model.mongodb.dto.FacultyIdWithTimeStamp
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.CoroutineCollection

class FacultyDaoImpl(override var collection: CoroutineCollection<MongoFaculty>) : FacultyDao {

    override suspend fun isFacultyAbbreviationAlreadyUsed(faculty: MongoFaculty) =
        collection.findOne(and(MongoFaculty::abbreviation eq faculty.abbreviation, MongoFaculty::id ne faculty.id)) != null


    override suspend fun isFacultyNameAlreadyUsed(faculty: MongoFaculty) =
        collection.findOne(and(MongoFaculty::name eq faculty.name, MongoFaculty::id ne faculty.id)) != null


    override suspend fun findFacultyByAbbreviation(abbreviation: String) = collection.findOne(MongoFaculty::abbreviation eq abbreviation)


    override suspend fun findFacultiesToDeleteLocally(localFacultyIdsWithTimestamp: List<FacultyIdWithTimeStamp>): List<String> {
        return localFacultyIdsWithTimestamp.map(FacultyIdWithTimeStamp::facultyId).let { facultyIds ->
            collection.find(MongoFaculty::id `in` facultyIds).toList().let { foundFaculties ->
                facultyIds.filter { id ->
                    foundFaculties.none {
                        it.id == id
                    }
                }
            }
        }
    }

    override suspend fun findFacultiesNotUpToDateOfUser(localFacultyIdsWithTimestamp: List<FacultyIdWithTimeStamp>): List<MongoFaculty> {
        return localFacultyIdsWithTimestamp.map(FacultyIdWithTimeStamp::facultyId).let { facultyIds ->
            localFacultyIdsWithTimestamp.map(::mapFacultyIdsToAreNotEqualToTimestamp).let { filters ->
                collection.find(
                    or(
                        or(filters),
                        MongoFaculty::id nin facultyIds
                    )
                ).toList()
            }
        }
    }

    private fun mapFacultyIdsToAreNotEqualToTimestamp(facultyIdWithTimeStamp: FacultyIdWithTimeStamp) = and(
        MongoFaculty::id eq facultyIdWithTimeStamp.facultyId,
        MongoFaculty::lastModifiedTimestamp ne facultyIdWithTimeStamp.lastModifiedTimestamp
    )


//    suspend fun generateSyncFacultiesResponse(facultyIdWithTimeStamp: List<FacultyIdWithTimeStamp>): SyncFacultiesResponse = withContext(IO) {
//        val localFacultyIds = facultyIdWithTimeStamp.map(FacultyIdWithTimeStamp::facultyId)
//
//        val facultiesToDownLoadAsync = async {
//            collection.find(or(buildFilter(facultyIdWithTimeStamp), MongoFaculty::id nin localFacultyIds)).toList()
//        }
//
//        val facultiesIdsToDeleteAsync = async {
//            collection.find(MongoFaculty::id `in` localFacultyIds).toList().let { allFaculties ->
//                localFacultyIds.filter { id -> allFaculties.none { it.id == id } }
//            }
//        }
//
//        val facultiesToUpdate: List<MongoFaculty>
//        val facultiesToInsert: List<MongoFaculty>
//
//        facultiesToDownLoadAsync.await().let {
//            facultiesToUpdate = it.filter { faculty -> localFacultyIds.any { id -> faculty.id == id } }
//            facultiesToInsert = it - facultiesToUpdate.toSet()
//        }
//
//        SyncFacultiesResponse(
//            facultiesToInsert = facultiesToInsert,
//            facultiesToUpdate = facultiesToUpdate,
//            facultyIdsToDelete = facultiesIdsToDeleteAsync.await()
//        )
//    }
//
//    private fun buildFilter(facultyIdWithTimeStamp: List<FacultyIdWithTimeStamp>) =
//        or(facultyIdWithTimeStamp.map { and(MongoFaculty::id eq it.facultyId, MongoFaculty::lastModifiedTimestamp ne it.lastModifiedTimestamp) })
}