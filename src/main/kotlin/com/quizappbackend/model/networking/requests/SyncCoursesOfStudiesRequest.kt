package com.quizappbackend.model.networking.requests

import com.quizappbackend.model.databases.dto.CourseOfStudiesIdWithTimeStamp
import kotlinx.serialization.Serializable

@Serializable
data class SyncCoursesOfStudiesRequest(
    val localCourseOfStudiesWithTimeStamp: List<CourseOfStudiesIdWithTimeStamp>
)