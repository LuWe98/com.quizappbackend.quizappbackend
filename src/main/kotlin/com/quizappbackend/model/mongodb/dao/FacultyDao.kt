package com.quizappbackend.model.mongodb.dao

import com.quizappbackend.model.mongodb.documents.MongoFaculty
import com.quizappbackend.model.mongodb.dto.FacultyIdWithTimeStamp

interface FacultyDao: BaseDao<MongoFaculty>  {

    suspend fun insertOrReplaceFaculty(faculty: MongoFaculty, id: String, upsert: Boolean = false): Boolean

    suspend fun isFacultyAbbreviationAlreadyUsed(faculty: MongoFaculty) : Boolean

    suspend fun isFacultyNameAlreadyUsed(faculty: MongoFaculty) : Boolean

    suspend fun findFacultyIdsToDeleteLocally(localFacultyIdsWithTimestamp: List<FacultyIdWithTimeStamp>): List<String>

    suspend fun findFacultiesNotUpToDateOfUser(localFacultyIdsWithTimestamp: List<FacultyIdWithTimeStamp>): List<MongoFaculty>

}