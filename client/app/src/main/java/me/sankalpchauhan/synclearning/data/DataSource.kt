package me.sankalpchauhan.synclearning.data

import kotlinx.coroutines.flow.Flow
import me.sankalpchauhan.synclearning.data.db.NotesEntity
import javax.inject.Qualifier


sealed class ApiResult<out T> {
    data class Success<out T>(val data: T) : ApiResult<T>()
    data class Error<out T>(val error: ApiError) : ApiResult<T>()
    val isSuccess: Boolean
        get() = this is Success<T>
}

data class ApiError(
    val code: Int,
    val message: String
)

interface LocalDataSource {
    suspend fun insertNote(note: NoteModel): Boolean
    suspend fun getAllNotes(): Flow<List<NotesEntity>>
    suspend fun getAllNotesSimple(): List<NotesEntity>
    suspend fun getNoteById(id: Int): NotesEntity?
    suspend fun getNoteByUUID(uuid: String): NotesEntity?
    suspend fun updateNote(note: NoteModel): Boolean
    suspend fun deleteNote(id: Int): Boolean
    suspend fun getSoftDeletedNotes(): List<NotesEntity>
}

interface NetworkDataSource {
    suspend fun insertNote(note: NoteModel): ApiResult<NoteApiResponse>
    suspend fun getAllNotes(): ApiResult<Flow<List<NoteApiResponse>>>
    suspend fun getAllNotesSimple(): ApiResult<List<NoteApiResponse>>
    suspend fun getNoteById(uuid: String): ApiResult<NoteApiResponse>
    suspend fun updateNote(note: NoteModel): ApiResult<NoteApiResponse>
    suspend fun deleteNote(uuid: String): ApiResult<NoteApiResponse>
}