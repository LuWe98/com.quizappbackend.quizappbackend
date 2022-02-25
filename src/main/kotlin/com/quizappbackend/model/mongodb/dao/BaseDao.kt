package com.quizappbackend.model.mongodb.dao

import com.quizappbackend.model.mongodb.documents.DocumentMarker
import org.litote.kmongo.`in`
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.eq
import kotlin.reflect.KProperty

interface BaseDao<T : DocumentMarker> {

    val collection: CoroutineCollection<T>

    suspend fun isCollectionEmpty() = collection.estimatedDocumentCount() == 0L

    suspend fun getAllEntries() = collection.find().toList()

    suspend fun insertOne(entry: T) = collection.insertOne(entry).wasAcknowledged()

    suspend fun insertMany(entries: List<T>) = collection.insertMany(entries).wasAcknowledged()

    suspend fun deleteOneById(id: String) = collection.deleteOneById(id).wasAcknowledged()

    suspend fun deleteManyById(id: String, idField: KProperty<String?>) = collection.deleteMany(idField eq id).wasAcknowledged()

    suspend fun deleteManyByIds(ids: List<String>, idField: KProperty<String?>) = collection.deleteMany(idField `in` ids).wasAcknowledged()

    suspend fun findOneById(id: String) = collection.findOneById(id)

    suspend fun findManyById(id: String, idField: KProperty<String?>) = collection.find(idField eq id).toList()

    suspend fun findManyByIds(ids: List<String>, idField: KProperty<String?>) = collection.find(idField `in` ids).toList()

}