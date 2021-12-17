package com.quizappbackend.model.databases.mongodb.dao

import com.quizappbackend.model.databases.dto.FacultyIdWithTimeStamp
import com.quizappbackend.model.databases.mongodb.documents.MongoFaculty
import com.quizappbackend.model.networking.responses.SyncFacultiesResponse
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.CoroutineCollection

class FacultyDao(override var collection: CoroutineCollection<MongoFaculty>) : BaseDao<MongoFaculty>(collection) {

    suspend fun isFacultyAbbreviationAlreadyUsed(faculty: MongoFaculty) =
        collection.findOne(and(MongoFaculty::abbreviation eq faculty.abbreviation, MongoFaculty::id ne faculty.id)) != null

    suspend fun isFacultyNameAlreadyUsed(faculty: MongoFaculty) =
        collection.findOne(and(MongoFaculty::name eq faculty.name, MongoFaculty::id ne faculty.id)) != null

    suspend fun findFacultyWithAbbreviation(abbreviation: String) = collection.findOne(MongoFaculty::abbreviation eq abbreviation)

    suspend fun generateSyncFacultiesResponse(facultyIdWithTimeStamp: List<FacultyIdWithTimeStamp>): SyncFacultiesResponse = withContext(IO) {
        val localFacultyIds = facultyIdWithTimeStamp.map(FacultyIdWithTimeStamp::facultyId)

        val facultiesToDownLoadAsync = async {
            collection.find(or(buildFilter(facultyIdWithTimeStamp), MongoFaculty::id nin localFacultyIds)).toList()
        }

        val facultiesIdsToDeleteAsync = async {
            collection.find(MongoFaculty::id `in` localFacultyIds).toList().let { allFaculties ->
                localFacultyIds.filter { id -> allFaculties.none { it.id == id } }
            }
        }

        val facultiesToUpdate: List<MongoFaculty>
        val facultiesToInsert: List<MongoFaculty>

        facultiesToDownLoadAsync.await().let {
            facultiesToUpdate = it.filter { faculty -> localFacultyIds.any { id -> faculty.id == id } }
            facultiesToInsert = it - facultiesToUpdate.toSet()
        }

        SyncFacultiesResponse(
            facultiesToInsert = facultiesToInsert,
            facultiesToUpdate = facultiesToUpdate,
            facultyIdsToDelete = facultiesIdsToDeleteAsync.await()
        )
    }

    private fun buildFilter(facultyIdWithTimeStamp: List<FacultyIdWithTimeStamp>) =
        or(facultyIdWithTimeStamp.map { and(MongoFaculty::id eq it.facultyId, MongoFaculty::lastModifiedTimestamp ne it.lastModifiedTimestamp) })
}