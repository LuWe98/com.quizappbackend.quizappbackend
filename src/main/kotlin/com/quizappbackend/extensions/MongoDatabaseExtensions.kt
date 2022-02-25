package com.quizappbackend.extensions

import com.mongodb.reactivestreams.client.ClientSession
import com.quizappbackend.model.mongodb.MongoRepository
import com.quizappbackend.model.mongodb.documents.DocumentMarker
import org.litote.kmongo.coroutine.CoroutineClient
import kotlin.reflect.KProperty1

suspend  fun CoroutineClient.runTransaction(block: suspend (ClientSession) -> (Unit)) = startSession().let { session ->
    session.startTransaction()
    block(session)
    session.commitTransaction()
}

suspend inline fun <reified T : DocumentMarker> MongoRepository.isCollectionEmpty() = isCollectionEmpty(T::class)

suspend inline fun <reified T : DocumentMarker> MongoRepository.getAllEntries() = getAllEntries(T::class)

suspend inline fun <reified T : DocumentMarker> MongoRepository.insertOne(document: T) = insertOne(document, T::class)

suspend inline fun <reified T : DocumentMarker> MongoRepository.insertMany(documents: List<T>) = insertMany(documents, T::class)

suspend inline fun <reified T : DocumentMarker> MongoRepository.findOneById(id: String) = findOneById(id, T::class)

suspend inline fun <reified T : DocumentMarker> MongoRepository.findManyByIds(ids: List<String>, idField: KProperty1<T, String?>) = findManyByIds(ids, idField, T::class)

suspend inline fun <reified T : DocumentMarker> MongoRepository.deleteOneById(id: String) = deleteOneById(id, T::class)

suspend inline fun <reified T : DocumentMarker> MongoRepository.deleteManyById(id: String, idField: KProperty1<T, String?>) = deleteManyById(id, idField, T::class)

suspend inline fun <reified T : DocumentMarker> MongoRepository.deleteManyByIds(ids: List<String>, idField: KProperty1<T, String?>) = deleteManyByIds(ids, idField, T::class)