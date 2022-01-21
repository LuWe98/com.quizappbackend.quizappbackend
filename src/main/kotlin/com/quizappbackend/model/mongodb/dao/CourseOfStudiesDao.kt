package com.quizappbackend.model.mongodb.dao

import com.quizappbackend.model.mongodb.documents.MongoCourseOfStudies
import com.quizappbackend.model.mongodb.dto.CourseOfStudiesIdWithTimeStamp

interface CourseOfStudiesDao: BaseDao<MongoCourseOfStudies> {

    suspend fun isCourseOfStudiesAbbreviationAlreadyUsed(courseOfStudies: MongoCourseOfStudies) : Boolean

    suspend fun findCourseOfStudiesByFacultyId(facultyId: String) : List<MongoCourseOfStudies>

    suspend fun findCourseOfStudiesToDeleteLocally(localCourseOfStudiesIdsWithTimeStamp: List<CourseOfStudiesIdWithTimeStamp>): List<String>

    suspend fun findCourseOfStudiesNotUpToDateOfUser(localCourseOfStudiesIdsWithTimeStamp: List<CourseOfStudiesIdWithTimeStamp>): List<MongoCourseOfStudies>

    suspend fun removeFacultyFromCourseOfStudies(facultyId: String): Boolean

}