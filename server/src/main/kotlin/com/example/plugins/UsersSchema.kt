package com.example.plugins

import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

@Serializable
data class ExposedNote(
    val uuid: String? = null,
    val uid: Int? = 0,
    val title: String,
    val description: String,
    val lastModified: Long = System.currentTimeMillis(),
    val version: Int = 1,
    val deviceId: String = UUID.randomUUID().toString()
)

class NoteService(database: Database) {
    object Note : UUIDTable() {
        val uid = integer("clientId").nullable()
        val title = varchar("title", length = 50)
        val description = varchar("description", length = 1000)
        val lastModified = long("last_modified").default(System.currentTimeMillis())
        val version = integer("version").default(1)
        val deviceId = varchar("deviceId", length = 100)
    }

    init {
        transaction(database) {
            SchemaUtils.create(Note)
        }
    }

    suspend fun create(note: ExposedNote): ExposedNote = dbQuery {
        val uuid = Note.insertAndGetId {
            it[title] = note.title
            it[description] = note.description
            it[uid] = note.uid
            it[lastModified] = note.lastModified
            it[deviceId] = note.deviceId
            it[version] = note.version
        }
        Note.selectAll().where { Note.id eq uuid }
            .map {
                ExposedNote(
                    uuid = uuid.toString(),
                    uid = it[Note.uid],
                    title = it[Note.title],
                    description = it[Note.description],
                    lastModified = it[Note.lastModified],
                    version = it[Note.version],
                    deviceId = it[Note.deviceId]
                )
            }.single()
    }

    suspend fun read(uuid: String): ExposedNote? {
        return dbQuery {
            Note.select { Note.id eq UUID.fromString(uuid) }
                .map {
                    ExposedNote(
                        uuid = it[Note.id].toString(),
                        uid = it[Note.uid],
                        title = it[Note.title],
                        description = it[Note.description],
                        lastModified = it[Note.lastModified],  // Handle lastModified if stored
                        version = it[Note.version] ,            // Handle version if stored
                        deviceId = it[Note.deviceId]
                    )
                }
                .singleOrNull()
        }
    }

    suspend fun read(deviceId: String, clientId: Int): ExposedNote? {
        return dbQuery {
            Note.select { (Note.deviceId eq deviceId) and (Note.uid eq clientId)  }
                .map {
                    ExposedNote(
                        uuid = it[Note.id].toString(),
                        uid = it[Note.uid],
                        title = it[Note.title],
                        description = it[Note.description],
                        lastModified = it[Note.lastModified],  // Handle lastModified if stored
                        version = it[Note.version],             // Handle version if stored
                        deviceId = it[Note.deviceId]
                    )
                }
                .singleOrNull()
        }
    }

    suspend fun readAll(): List<ExposedNote> {
        return dbQuery {
            Note.selectAll()
                .map {
                    ExposedNote(
                        uuid = it[Note.id].toString(),
                        uid = it[Note.uid],
                        title = it[Note.title],
                        description = it[Note.description],
                        lastModified = it[Note.lastModified],  // Handle lastModified if stored
                        version = it[Note.version],             // Handle version if stored
                        deviceId = it[Note.deviceId]
                    )
                }
        }
    }


    suspend fun createBatch(notes: List<ExposedNote>): List<Int?> = dbQuery {
        Note.batchInsert(notes) { note ->
            this[Note.title] = note.title
            this[Note.description] = note.description
            this[Note.uid] = note.uid  // Handle nullable UID
            this[Note.deviceId] = note.deviceId
        }.map { it[Note.uid] }
    }

    suspend fun update(uuid: String, note: ExposedNote): ExposedNote =
        dbQuery {
            Note.update({ Note.id eq UUID.fromString(uuid) }) {
                it[title] = note.title
                it[description] = note.description
                it[uid] = note.uid
                it[deviceId] = note.deviceId
            }
            Note.selectAll().where { Note.id eq UUID.fromString(uuid) }
                .map {
                    ExposedNote(
                        uuid = uuid.toString(),
                        uid = it[Note.uid],
                        title = it[Note.title],
                        description = it[Note.description],
                        lastModified = it[Note.lastModified],
                        version = it[Note.version],
                        deviceId = it[Note.deviceId]
                    )
                }.single()
        }


    suspend fun delete(uuid: String): ExposedNote =
        dbQuery {
            val note = Note.selectAll().where{Note.id eq UUID.fromString(uuid)}
                .map {
                    ExposedNote(
                        uuid = it[Note.id].toString(),
                        uid = it[Note.uid],
                        title = it[Note.title],
                        description = it[Note.description],
                        lastModified = it[Note.lastModified],  // Handle lastModified if stored
                        version = it[Note.version],             // Handle version if stored
                        deviceId = it[Note.deviceId]
                    )
                }.single()
            Note.deleteWhere { Note.id eq UUID.fromString(uuid) }
            note
        }

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}

