package com.quizappbackend.model.databases.mongodb.dao

import com.mongodb.client.model.ReplaceOneModel
import com.mongodb.client.model.ReplaceOptions
import com.quizappbackend.model.databases.mongodb.documents.DocumentMarker
import org.bson.conversions.Bson
import org.litote.kmongo.`in`
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.eq
import kotlin.reflect.KProperty

abstract class BaseDao <T: DocumentMarker>(open var collection: CoroutineCollection<T>){

    suspend fun isCollectionEmpty() = collection.estimatedDocumentCount() == 0L

    suspend fun getAllEntries() = collection.find().toList()

    suspend fun insertOne(entry: T) = collection.insertOne(entry).wasAcknowledged()

    suspend fun insertMany(entries: List<T>) = collection.insertMany(entries).wasAcknowledged()

    suspend fun checkIfExistsWithId(id: String) = collection.findOneById(id) != null

    suspend fun deleteOneById(id: String) = collection.deleteOneById(id).wasAcknowledged()

    suspend fun deleteManyById(id: String, idField : KProperty<String?>) = collection.deleteMany(idField eq id).wasAcknowledged()

    suspend fun deleteManyByIds(ids : List<String>, idField : KProperty<String?>) = collection.deleteMany(idField `in` ids).wasAcknowledged()

    suspend fun findOneById(id: String) = collection.findOneById(id)

    suspend fun findManyById(id: String, idField : KProperty<String?>) = collection.find(idField eq id).toList()

    suspend fun findManyByIds(ids: List<String>, idField : KProperty<String?>) = collection.find(idField `in` ids).toList()

    suspend fun replaceOneById(entry: T, id: String, upsert: Boolean) = collection.replaceOneById(id, entry, ReplaceOptions().upsert(upsert)).wasAcknowledged()

    suspend inline fun replaceOneWith(entry: T, upsert: Boolean, crossinline replaceByFilter: ((T) -> Bson)) =
        collection.replaceOne(replaceByFilter(entry), entry, ReplaceOptions().upsert(upsert)).wasAcknowledged()

    suspend fun replaceManyById(entries: List<T>, idField : KProperty<String?>, upsert: Boolean)  : Boolean {
        val bulkReplace = entries.map {
            ReplaceOneModel(idField eq idField.call(it), it, ReplaceOptions().upsert(upsert))
        }
        return collection.bulkWrite(bulkReplace).wasAcknowledged()
    }

    suspend inline fun replaceManyByFilter(entries: List<T>, upsert: Boolean, crossinline replaceByFilter: ((T) -> Bson))  : Boolean {
        val bulkReplace = entries.map {
            ReplaceOneModel(replaceByFilter(it), it, ReplaceOptions().upsert(upsert))
        }
        return collection.bulkWrite(bulkReplace).wasAcknowledged()
    }

    suspend fun updateOneById(id: String, newValue: T) = collection.updateOneById(id, newValue).wasAcknowledged()

}