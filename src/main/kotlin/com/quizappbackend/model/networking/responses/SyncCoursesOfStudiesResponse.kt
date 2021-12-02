package com.quizappbackend.model.networking.responses

import com.quizappbackend.model.databases.mongodb.documents.faculty.MongoCourseOfStudies
import kotlinx.serialization.Serializable

@Serializable
data class SyncCoursesOfStudiesResponse(
    val coursesOfStudiesToInsert: List<MongoCourseOfStudies>,
    val coursesOfStudiesToUpdate: List<MongoCourseOfStudies>,
    val courseOfStudiesIdsToDelete: List<String>
)