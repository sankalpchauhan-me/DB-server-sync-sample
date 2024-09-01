package me.sankalpchauhan.synclearning.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.sankalpchauhan.synclearning.data.db.SyncStatus
import javax.inject.Inject

interface NotesRepository{
    suspend fun insertNote(noteModel: NoteModel)
    suspend fun getAllNotes(): Flow<List<NoteModel>>
    suspend fun getNoteById(id: Int): NoteModel?
    suspend fun updateNote(noteModel: NoteModel)
    suspend fun deleteNote(id: Int)
}

class DefaultNotesRepository @Inject constructor(
    private val localDataSource: LocalDataSource,
    private val networkDataSource: NetworkDataSource
): NotesRepository {

    override suspend fun insertNote(noteModel: NoteModel) {
        localDataSource.insertNote(noteModel)
    }

    override suspend fun getAllNotes(): Flow<List<NoteModel>> {
        return localDataSource.getAllNotes().map { notesList ->
            notesList.map { notesEntity ->
                NoteModel(
                    id = notesEntity.uid,
                    title = notesEntity.title,
                    description = notesEntity.description,
                    syncStatus = notesEntity.syncStatus,
                    uuid = notesEntity.serverId
                )
            }
        }
    }

    override suspend fun getNoteById(id: Int): NoteModel? {
        return localDataSource.getNoteById(id)?.toNoteModel()
    }

    override suspend fun updateNote(noteModel: NoteModel) {
        localDataSource.updateNote(noteModel.copy(syncStatus = SyncStatus.PENDING))
    }

    override suspend fun deleteNote(id: Int) {
        localDataSource.deleteNote(id)
    }

}