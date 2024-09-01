package me.sankalpchauhan.synclearning.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import me.sankalpchauhan.synclearning.data.NoteModel

@Dao
interface NotesDAO {

    @Query("SELECT * FROM notes WHERE isDeleted=0")
    fun getAll(): Flow<List<NotesEntity>>

    @Query("SELECT * FROM notes WHERE isDeleted=0")
    fun getAllSimple(): List<NotesEntity>

    @Query("SELECT * FROM notes WHERE uid IN (:noteIds) AND isDeleted=0")
    fun loadAllByIds(noteIds: IntArray): Flow<List<NotesEntity>>

    @Query("SELECT * FROM notes WHERE uid = :id")
    fun getNoteById(id: Int): NotesEntity?

    @Query("SELECT * FROM notes WHERE serverId = :id")
    fun getNoteByUUID(id: String): NotesEntity?

    @Insert
    fun insertAll(vararg notes: NotesEntity)

    @Delete
    fun delete(note: NotesEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(note: NotesEntity)

    @Update
    fun update(note: NotesEntity)

    @Query("UPDATE notes SET isDeleted = 1 WHERE uid = :noteId")
    fun softDelete(noteId: Int)

    @Query("SELECT * FROM notes WHERE isDeleted = 1")
    fun getSoftDeletedNotes():  List<NotesEntity>

    @Transaction
    fun updateNoteWithTimestamp(id: Int, newNote: NoteModel) {
        val note = getNoteById(id)
        note?.let {
            val updatedNote = it.copy(
                title = newNote.title,
                description = newNote.description,
                version = it.version + 1,
                lastModified = System.currentTimeMillis(),
                serverId = newNote.uuid,
                syncStatus = newNote.syncStatus
            )
            update(updatedNote)
        }
    }

}