package com.quizappbackend.model.mongodb.dao

import com.mongodb.client.model.ReplaceOptions
import com.quizappbackend.model.mongodb.documents.MongoFaculty
import com.quizappbackend.model.mongodb.dto.FacultyIdWithTimeStamp
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.CoroutineCollection

class FacultyDaoImpl(override var collection: CoroutineCollection<MongoFaculty>) : FacultyDao {

    override suspend fun insertOrReplaceFaculty(faculty: MongoFaculty, id: String, upsert: Boolean) =
        collection.replaceOneById(id, faculty, ReplaceOptions().upsert(upsert)).wasAcknowledged()

    override suspend fun isFacultyAbbreviationAlreadyUsed(faculty: MongoFaculty) =
        collection.findOne(and(MongoFaculty::abbreviation eq faculty.abbreviation, MongoFaculty::id ne faculty.id)) != null


    override suspend fun isFacultyNameAlreadyUsed(faculty: MongoFaculty) =
        collection.findOne(and(MongoFaculty::name eq faculty.name, MongoFaculty::id ne faculty.id)) != null


    override suspend fun findFacultyIdsToDeleteLocally(localFacultyIdsWithTimestamp: List<FacultyIdWithTimeStamp>): List<String> {
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
}