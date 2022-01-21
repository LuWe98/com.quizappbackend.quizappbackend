package com.quizappbackend.model.mongodb.dao

import com.quizappbackend.model.mongodb.documents.MongoFaculty
import com.quizappbackend.model.mongodb.dto.FacultyIdWithTimeStamp

interface FacultyDao: BaseDao<MongoFaculty>  {

    suspend fun isFacultyAbbreviationAlreadyUsed(faculty: MongoFaculty) : Boolean

    suspend fun isFacultyNameAlreadyUsed(faculty: MongoFaculty) : Boolean

    suspend fun findFacultyByAbbreviation(abbreviation: String) : MongoFaculty?

    suspend fun findFacultiesToDeleteLocally(localFacultyIdsWithTimestamp: List<FacultyIdWithTimeStamp>): List<String>

    suspend fun findFacultiesNotUpToDateOfUser(localFacultyIdsWithTimestamp: List<FacultyIdWithTimeStamp>): List<MongoFaculty>

}