package com.quizappbackend.routing

import com.quizappbackend.authentication.JwtAuth.userPrinciple
import com.quizappbackend.model.mongodb.properties.Role.*
import com.quizappbackend.routing.ApiPaths.*
import com.quizappbackend.routing.services.*
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Routing.initRoutes(
    facultyRouteService: FacultyRouteService,
    courseOfStudiesRouteService: CourseOfStudiesRouteService,
    userRouteService: UserRouteService,
    questionnaireRouteService: QuestionnaireRouteService,
    filledQuestionnaireRouteService: FilledQuestionnaireRouteService
) {
    facultyRouting(facultyRouteService)
    courseOfStudiesRouting(courseOfStudiesRouteService)
    userRouting(userRouteService)
    questionnaireRouting(questionnaireRouteService)
    filledQuestionnaireRouting(filledQuestionnaireRouteService)
}


private fun Routing.facultyRouting(service: FacultyRouteService) = FacultyPaths.apply {
    authenticate(USER.name) {
        post(SYNC) {
            call.respond(service.handleSyncRequest(call.receive()))
        }
    }

    authenticate(ADMIN.name) {
        post(INSERT) {
            call.respond(service.handleInsertRequest(call.receive()))
        }

        delete(DELETE) {
            call.respond(service.handleDeleteRequest(call.receive()))
        }
    }
}


private fun Routing.courseOfStudiesRouting(service: CourseOfStudiesRouteService) = CourseOfStudiesPaths.apply {
    authenticate(USER.name) {
        post(SYNC) {
            call.respond(service.handleSyncRequest(call.receive()))
        }
    }

    authenticate(ADMIN.name) {
        post(INSERT) {
            call.respond(service.handleInsertRequest(call.receive()))
        }

        delete(DELETE) {
            call.respond(service.handleDeleteRequest(call.receive()))
        }
    }
}


private fun Routing.userRouting(service: UserRouteService) = UserPaths.apply {

    post(LOGIN) {
        call.respond(service.handleLoginUserRequest(call.receive()))
    }

    post(REGISTER) {
        call.respond(service.handleRegisterUserRequest(call.receive()))
    }

    post(REFRESH_TOKEN) {
        call.respond(service.handleGetRefreshTokenRequest(call.receive()))
    }

    authenticate(USER.name) {
        delete(DELETE_SELF) {
            call.respond(service.handleDeleteOwnUserRequest(userPrinciple))
        }

        post(SYNC) {
            call.respond(service.handleSyncUserDataRequest(userPrinciple))
        }

        post(AUTHORS_PAGED) {
            call.respond(service.handleGetPagedAuthorsRequest(call.receive()))
        }

        post(UPDATE_PASSWORD) {
            call.respond(service.handleUpdatePasswordRequest(userPrinciple, call.receive()))
        }

        post(UPDATE_CAN_SHARE_QUESTIONNAIRE_WITH) {
            call.respond(service.handleUpdateUserCanShareQuestionnaireWithRequest(userPrinciple))
        }
    }

    authenticate(ADMIN.name) {
        post(USERS_PAGED_ADMIN) {
            call.respond(service.handleGetPagedUsersRequest(call.receive()))
        }

        post(UPDATE_ROLE) {
            call.respond(service.handleUpdateUserRoleRequest(call.receive()))
        }

        delete(DELETE_USER) {
            call.respond(service.handleDeleteUserRequest(call.receive()))
        }

        post(CREATE) {
            call.respond(service.handleCreateUserRequest(call.receive()))
        }
    }
}


private fun Routing.filledQuestionnaireRouting(service: FilledQuestionnaireRouteService) = FilledQuestionnairePaths.apply {
    authenticate(USER.name) {
        post(INSERT_SINGLE) {
            call.respond(service.handleInsertRequest(userPrinciple, call.receive()))
        }

        post(INSERT_MULTIPLE) {
            call.respond(service.handleInsertMultipleRequest(userPrinciple, call.receive()))
        }

        delete(DELETE) {
            call.respond(service.handleDeleteRequest(userPrinciple, call.receive()))
        }

//        post(GENERATE_RANDOM) {
//            call.respond(service.handleGenerateRandomRequest(userPrinciple, call.receive()))
//        }
    }
}


private fun Routing.questionnaireRouting(service: QuestionnaireRouteService) = QuestionnairePaths.apply {
    authenticate(USER.name) {
        post(SYNC) {
            call.respond(service.handleSyncQuestionnaireRequest(userPrinciple, call.receive()))
        }

        post(INSERT) {
            call.respond(service.handleInsertRequest(call.receive()))
        }

        delete(DELETE) {
            call.respond(service.handleDeleteRequest(call.receive()))
        }

        post(PAGED) {
            call.respond(service.handleGetPagedQuestionnairesRequest(userPrinciple, call.receive()))
        }

        post(DOWNLOAD) {
            call.respond(service.handleDownloadQuestionnaireRequest(userPrinciple, call.receive()))
        }

        post(SHARE) {
            call.respond(service.handleShareRequest(call.receive()))
        }

//        post(GENERATE_RANDOM) {
//            call.respond(service.handleGenerateRandomQuestionnairesRequest(userPrinciple, call.receive()))
//        }
    }

    authenticate(CREATOR.name) {
        post(UPDATE_VISIBILITY) {
            call.respond(service.handleChangeVisibilityRequest(call.receive()))
        }
    }
}