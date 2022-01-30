package com.quizappbackend

import com.quizappbackend.model.ktor.BackendRequest
import com.quizappbackend.model.ktor.paging.BrowsableQuestionnairePageKeys
import com.quizappbackend.model.mongodb.MongoRepository
import com.quizappbackend.model.mongodb.dto.MongoBrowsableQuestionnaire
import com.quizappbackend.model.mongodb.dto.BrowsableQuestionnaireOrderBy
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.io.File
import java.io.OutputStreamWriter
import kotlin.system.measureTimeMillis

object PagingTests {

    private const val USER_ID = ""
    private const val SEARCH_STRING = ""
    private var orderBy = BrowsableQuestionnaireOrderBy.TITLE

    private fun createEmptySkipRequest(limit: Int, page: Int) = BackendRequest.GetPagedQuestionnairesRequest(
        limit = limit,
        page = page,
        searchString = SEARCH_STRING,
        questionnaireIdsToIgnore = emptyList(),
        facultyIds = emptyList(),
        courseOfStudiesIds = emptyList(),
        authorIds = emptyList(),
        orderBy = orderBy,
        ascending = true
    )

    private fun createEmptyIdRequest(limit: Int, lastTitle: String, lastId: String, lastTimeStamp: Long) = BackendRequest.GetPagedQuestionnairesWithPageKeysRequest(
        lastPageKeys = BrowsableQuestionnairePageKeys(id = lastId, title = lastTitle, timeStamp = lastTimeStamp),
        limit = limit,
        searchString = SEARCH_STRING,
        questionnaireIdsToIgnore = emptyList(),
        facultyIds = emptyList(),
        courseOfStudiesIds = emptyList(),
        authorIds = emptyList(),
        orderBy = orderBy,
        ascending = true
    )

    suspend fun comparison(
        repo: MongoRepository,
        limit: Int,
        printToConsole: Boolean = false,
        printToFile: Boolean = false,
        maxPagesToLoad: Int = Int.MAX_VALUE,
        useOrderBy: BrowsableQuestionnaireOrderBy = BrowsableQuestionnaireOrderBy.TITLE
    ) = withContext(IO) {
        orderBy = useOrderBy

        var list: List<MongoBrowsableQuestionnaire>
        var questions: List<MongoBrowsableQuestionnaire> = emptyList()
        var nextKeys = BrowsableQuestionnairePageKeys()

        var totalTimePage: Long = 0
        var totalTimeString: Long = 0

        var page = 1

        var writer: OutputStreamWriter?  = null

        if(printToFile) {
            writer = File("PagingComparison_${orderBy.name}_${maxPagesToLoad}.csv").writer()
        }

        writer?.write("PAGE;TIME SKIP;TIME ID\n")

        while (true) {
            val skipRequest = createEmptySkipRequest(limit, page)
            val idRequest = createEmptyIdRequest(
                limit,
                nextKeys.title,
                nextKeys.id,
                nextKeys.timeStamp
            )

            val timePage = measureTimeMillis {
                list = repo.getQuestionnairesPaged(
                    userId = USER_ID,
                    request = skipRequest
                )
            }

            val timeString = measureTimeMillis {
                val first = async {
                    questions = repo.getQuestionnairesPagedWithPageKeys(
                        userId = USER_ID,
                        request = idRequest
                    )
                }
                val second = async {
                    repo.getPreviousPageKeys(
                        userId = USER_ID,
                        request = idRequest
                    )
                }
                first.await()
                second.await()
            }

            questions.lastOrNull()?.let { lastQuestionnaire ->
                nextKeys = BrowsableQuestionnairePageKeys(
                    lastQuestionnaire.id,
                    lastQuestionnaire.title,
                    lastQuestionnaire.lastModifiedTimestamp
                )
            }

            if(printToConsole) {
                println("PAGE $page: ${if (timePage < timeString) "PAGE FASTER" else "STRING FASTER"} - TIME PAGE: $timePage - TIME STRING: $timeString")
            }

            writer?.write("$page;$timePage;$timeString\n")

            totalTimePage += timePage
            totalTimeString += timeString

            page++
            if (list.isEmpty() || questions.isEmpty() || page >= maxPagesToLoad) {
                break
            }
        }

        writer?.flush()
        writer?.close()

        println("TOTAL TIME NEEDED PAGE: $totalTimePage")
        println("TOTAL TIME NEEDED STRING: $totalTimeString")
    }


    suspend fun measureTimePageCount(
        repo: MongoRepository,
        limit: Int,
        maxPagesToLoad: Int = Int.MAX_VALUE,
        useOrderBy: BrowsableQuestionnaireOrderBy = BrowsableQuestionnaireOrderBy.TITLE
    ) {
        orderBy = useOrderBy
        var list: List<MongoBrowsableQuestionnaire>

        var totalTime: Long = 0
        var criteria = true
        var page = 1

        while (criteria) {
            val skipRequest = createEmptySkipRequest(limit, page)

            val time = measureTimeMillis {
                list = repo.getQuestionnairesPaged(
                    userId = USER_ID,
                    request = skipRequest
                )
            }

//        list.forEach {
//            println("TITLE: ${it.title}")
//        }

            totalTime += time
            println("TIME NEEDED PAGE $page: $time")
            println()

            page++
            if (list.isEmpty() || page >= maxPagesToLoad) {
                criteria = false
            }
        }

        println("TOTAL TIME NEEDED $totalTime")
    }


    suspend fun measureTimeStringIndex(
        repo: MongoRepository,
        limit: Int,
        maxPagesToLoad: Int = Int.MAX_VALUE,
        useOrderBy: BrowsableQuestionnaireOrderBy = BrowsableQuestionnaireOrderBy.TITLE
    ) = withContext(IO) {

        orderBy = useOrderBy

        var idRequest = createEmptyIdRequest(limit, "", "", 0)

        var questions: List<MongoBrowsableQuestionnaire> = repo.getQuestionnairesPagedWithPageKeys(
            userId = USER_ID,
            request = idRequest
        )

        var nextKeys: BrowsableQuestionnairePageKeys? = questions.lastOrNull()?.let {
            BrowsableQuestionnairePageKeys(it.id, it.title)
        }

        var previousKey = repo.getPreviousPageKeys(
            userId = USER_ID,
            request = idRequest
        )

        var totalTimeAsync = 0L
        var page = 1

        while (questions.isNotEmpty()) {
//            println("QUESTIONS:")
//            questions.forEach {
//                println("${it.title} | ID: ${it.id} | AUTHOR INFO: ${it.authorInfo.userName}")
//            }
//            println("Previous key: $previousKey")
//            println("Next key: $nextKeys")
//            println()

            idRequest = createEmptyIdRequest(limit, nextKeys!!.title, nextKeys.id, nextKeys.timeStamp)

            val timeAsync = measureTimeMillis {
                val first = async {
                    questions = repo.getQuestionnairesPagedWithPageKeys(
                        userId = "",
                        request = idRequest
                    )
                }
                val second = async {
                    previousKey = repo.getPreviousPageKeys(
                        userId = SEARCH_STRING,
                        request = idRequest
                    )
                }
                first.await()
                second.await()
            }

            questions.lastOrNull()?.let { lastQuestionnaire ->
                nextKeys = BrowsableQuestionnairePageKeys(
                    lastQuestionnaire.id,
                    lastQuestionnaire.title,
                    lastQuestionnaire.lastModifiedTimestamp
                )
            }

            println("ASYNC TIME NEEDED PAGE $page: $timeAsync")
            totalTimeAsync += timeAsync
            page++
            if (page >= maxPagesToLoad) {
                break
            }
        }

        println("TOTAL ASYNC TIME NEEDED: $totalTimeAsync")
    }
}