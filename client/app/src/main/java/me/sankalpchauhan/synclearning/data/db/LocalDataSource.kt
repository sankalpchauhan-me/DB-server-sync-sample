package me.sankalpchauhan.synclearning.data.db

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.sankalpchauhan.synclearning.data.LocalDataSource
import me.sankalpchauhan.synclearning.data.NoteModel
import javax.inject.Inject

class DefaultLocalDataSource @Inject constructor(
    private val appDatabase: AppDatabase
) : LocalDataSource {
    override suspend fun insertNote(note: NoteModel): Boolean {
        appDatabase.notesDao().insert(NotesEntity(title = note.title, description = note.description, serverId = note.uuid, syncStatus = note.syncStatus))
        return true
    }

    override suspend fun getAllNotes(): Flow<List<NotesEntity>> {
        return appDatabase.notesDao().getAll()
    }

    override suspend fun getAllNotesSimple(): List<NotesEntity> {
        return appDatabase.notesDao().getAllSimple()
    }

    override suspend fun getNoteById(id: Int): NotesEntity? {
        return appDatabase.notesDao().getNoteById(id)
    }

    override suspend fun getNoteByUUID(uuid: String): NotesEntity? {
        val dbNote = appDatabase.notesDao().getNoteByUUID(uuid)
        return dbNote
    }

    override suspend fun updateNote(note: NoteModel): Boolean {
        note.id?.let {
            appDatabase.notesDao().updateNoteWithTimestamp(it, note)
            return true
        }?:{
            Log.d("LocalDataSource", "updateNote: Note id is null")
        }
        return false
    }

    override suspend fun deleteNote(id: Int): Boolean {
        appDatabase.notesDao().softDelete(id)
        return true
    }

    override suspend fun getSoftDeletedNotes(): List<NotesEntity> {
        return appDatabase.notesDao().getSoftDeletedNotes()
    }
}