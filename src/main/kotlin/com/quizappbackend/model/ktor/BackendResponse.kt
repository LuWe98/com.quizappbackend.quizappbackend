package com.quizappbackend.model.ktor

import com.quizappbackend.model.ktor.paging.BrowsableQuestionnairePageKeys
import com.quizappbackend.model.mongodb.documents.MongoCourseOfStudies
import com.quizappbackend.model.mongodb.documents.MongoFaculty
import com.quizappbackend.model.mongodb.documents.MongoFilledQuestionnaire
import com.quizappbackend.model.mongodb.documents.MongoQuestionnaire
import com.quizappbackend.model.mongodb.dto.MongoBrowsableQuestionnaire
import com.quizappbackend.model.mongodb.properties.Role
import kotlinx.serialization.Serializable

sealed class BackendResponse {

    @Serializable
    class BasicResponse(val isSuccessful: Boolean): BackendResponse()

    @Serializable
    data class GetPagedQuestionnairesWithPageKeysResponse(
        val previousKeys: BrowsableQuestionnairePageKeys?,
        val questionnaires: List<MongoBrowsableQuestionnaire>
    )

    @Serializable
    class TestResponse(val isSuccessful: Boolean, val errorType: TestResponseErrorType? = null): BackendResponse() {
        enum class TestResponseErrorType {
            ERROR_TYPE_ONE,
            ERROR_TYPE_TWO
        }
    }


    @Serializable
    data class ChangePasswordResponse(
        val newToken: String? = null,
        val responseType: ChangePasswordResponseType
    ): BackendResponse() {
        enum class ChangePasswordResponseType{
            SUCCESSFUL,
            NOT_ACKNOWLEDGED
        }
    }


    @Serializable
    data class ChangeQuestionnaireVisibilityResponse(
        val responseType: ChangeQuestionnaireVisibilityResponseType
    ): BackendResponse() {
        enum class ChangeQuestionnaireVisibilityResponseType{
            SUCCESSFUL,
            NOT_ACKNOWLEDGED
        }
    }


    @Serializable
    data class CreateUserResponse(
        val responseType: CreateUserResponseType,
    ): BackendResponse() {
        enum class CreateUserResponseType {
            CREATION_SUCCESSFUL,
            NOT_ACKNOWLEDGED,
            USER_ALREADY_EXISTS
        }
    }


    @Serializable
    data class DeleteCourseOfStudiesResponse(
        val responseType: DeleteCourseOfStudiesResponseType
    ): BackendResponse() {
        enum class  DeleteCourseOfStudiesResponseType {
            SUCCESSFUL,
            NOT_ACKNOWLEDGED
        }
    }


    @Serializable
    data class DeleteFacultyResponse(
        val responseType: DeleteFacultyResponseType
    ): BackendResponse() {
        enum class  DeleteFacultyResponseType {
            SUCCESSFUL,
            NOT_ACKNOWLEDGED
        }
    }


    @Serializable
    data class DeleteFilledQuestionnaireResponse(
        val responseType: DeleteFilledQuestionnaireResponseType
    ): BackendResponse() {
        enum class  DeleteFilledQuestionnaireResponseType {
            SUCCESSFUL,
            NOT_ACKNOWLEDGED
        }
    }


    @Serializable
    data class DeleteQuestionnaireResponse(
        val responseType: DeleteQuestionnaireResponseType
    ): BackendResponse() {
        enum class  DeleteQuestionnaireResponseType {
            SUCCESSFUL,
            NOT_ACKNOWLEDGED
        }
    }


    @Serializable
    data class DeleteUserResponse(
        val responseType: DeleteUserResponseType,
    ): BackendResponse() {
        enum class DeleteUserResponseType {
            SUCCESSFUL,
            NOT_ACKNOWLEDGED
        }
    }


    @Serializable
    data class GetQuestionnaireResponse(
        val responseType: GetQuestionnaireResponseType,
        val mongoQuestionnaire: MongoQuestionnaire? = null
    ): BackendResponse() {
        enum class GetQuestionnaireResponseType {
            SUCCESSFUL,
            QUESTIONNAIRE_NOT_FOUND,
        }
    }


    @Serializable
    data class InsertCourseOfStudiesResponse(
        val responseType: InsertCourseOfStudiesResponseType
    ): BackendResponse() {
        enum class  InsertCourseOfStudiesResponseType {
            SUCCESSFUL,
            ABBREVIATION_ALREADY_USED,
            NOT_ACKNOWLEDGED
        }
    }


    @Serializable
    data class InsertFacultyResponse(
        val responseType: InsertFacultyResponseType
    ): BackendResponse() {
        enum class  InsertFacultyResponseType {
            SUCCESSFUL,
            ABBREVIATION_ALREADY_USED,
            NAME_ALREADY_USED,
            NOT_ACKNOWLEDGED
        }
    }


    @Serializable
    data class InsertFilledQuestionnaireResponse(
        val responseType: InsertFilledQuestionnaireResponseType
    ): BackendResponse() {
        enum class  InsertFilledQuestionnaireResponseType {
            SUCCESSFUL,
            QUESTIONNAIRE_DOES_NOT_EXIST_ANYMORE,
            NOT_ACKNOWLEDGED
        }
    }


    @Serializable
    data class InsertFilledQuestionnairesResponse(
        val notInsertedQuestionnaireIds: List<String> = emptyList(),
        val responseType: InsertFilledQuestionnairesResponseType
    ): BackendResponse() {
        enum class  InsertFilledQuestionnairesResponseType {
            SUCCESSFUL,
            NOT_ACKNOWLEDGED
        }
    }


    @Serializable
    data class InsertQuestionnairesResponse(
        val responseType: InsertQuestionnairesResponseType
    ): BackendResponse() {
        enum class  InsertQuestionnairesResponseType {
            SUCCESSFUL,
            NOT_ACKNOWLEDGED
        }
    }


    @Serializable
    data class LoginUserResponse(
        val userId: String? = null,
        val role: Role? = null,
        val lastModifiedTimeStamp: Long? = null,
        val token: String? = null,
        val responseType: LoginUserResponseType,
    ): BackendResponse() {
        enum class LoginUserResponseType {
            LOGIN_SUCCESSFUL,
            USER_NAME_OR_PASSWORD_WRONG
        }
    }


    @Serializable
    data class RefreshJwtTokenResponse(
        val token: String?
    ): BackendResponse()

    @Serializable
    data class RegisterUserResponse(
        val responseType: RegisterUserResponseType,
    ): BackendResponse() {
        enum class RegisterUserResponseType {
            REGISTER_SUCCESSFUL,
            NOT_ACKNOWLEDGED,
            USER_ALREADY_EXISTS
        }
    }


    @Serializable
    data class ShareQuestionnaireWithUserResponse(
        val responseType: ShareQuestionnaireWithUserResponseType
    ): BackendResponse() {
        enum class ShareQuestionnaireWithUserResponseType {
            SUCCESSFUL,
            ALREADY_SHARED_WITH_USER,
            USER_DOES_NOT_EXIST,
            QUESTIONNAIRE_DOES_NOT_EXIST,
            NOT_ACKNOWLEDGED,
            USER_IS_OWNER_OF_QUESTIONNAIRE
        }
    }


    @Serializable
    data class SyncCoursesOfStudiesResponse(
        val coursesOfStudiesToInsert: List<MongoCourseOfStudies>,
        val coursesOfStudiesToUpdate: List<MongoCourseOfStudies>,
        val courseOfStudiesIdsToDelete: List<String>
    ): BackendResponse()


    @Serializable
    data class SyncFacultiesResponse(
        val facultiesToInsert: List<MongoFaculty>,
        val facultiesToUpdate: List<MongoFaculty>,
        val facultyIdsToDelete: List<String>
    ): BackendResponse()


    @Serializable
    data class SyncQuestionnairesResponse(
        val mongoQuestionnaires: List<MongoQuestionnaire>,
        val mongoFilledQuestionnaires: List<MongoFilledQuestionnaire>,
        val questionnaireIdsToUnsync: List<String>
    ): BackendResponse()


    @Serializable
    data class SyncUserDataResponse(
        val role: Role? = null,
        val lastModifiedTimestamp: Long? = null,
        val responseType: SyncUserDataResponseType
    ): BackendResponse() {
        enum class SyncUserDataResponseType {
            DATA_UP_TO_DATE,
            DATA_CHANGED
        }
    }


    @Serializable
    data class UpdateUserResponse(
        val responseType: UpdateUserResponseType,
    ): BackendResponse() {
        enum class UpdateUserResponseType {
            UPDATE_SUCCESSFUL,
            NOT_ACKNOWLEDGED,
            USERNAME_ALREADY_TAKEN,
            LAST_CHANGE_TO_CLOSE,
            USER_DOES_NOT_EXIST
        }
    }
}