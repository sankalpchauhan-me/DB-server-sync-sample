package me.sankalpchauhan.synclearning.data.network

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import me.sankalpchauhan.synclearning.data.ApiError
import me.sankalpchauhan.synclearning.data.ApiResult
import me.sankalpchauhan.synclearning.data.NetworkDataSource
import me.sankalpchauhan.synclearning.data.NoteApiResponse
import me.sankalpchauhan.synclearning.data.NoteModel
import me.sankalpchauhan.synclearning.data.db.SyncStatus
import javax.inject.Inject

class DefaultNetworkDataSource @Inject constructor(
    private val notesService: NotesService
) : NetworkDataSource {
    override suspend fun insertNote(note: NoteModel): ApiResult<NoteApiResponse> {
        return try {
            val response = notesService.insertNote(note.toNoteRequest())
            ApiResult.Success(response)
        } catch (e: Exception) {
            ApiResult.Error(ApiError(code = 500, message = e.message ?: "Unknown error"))
        }
    }

    override suspend fun getAllNotes(): ApiResult<Flow<List<NoteApiResponse>>> {
        return try {
            val flow = flow {
                val notes = notesService.getAllNotes()
                emit(notes)
            }
            ApiResult.Success(flow)
        } catch (e: Exception) {
            ApiResult.Error(ApiError(code = 500, message = e.message ?: "Unknown error"))
        }
    }

    override suspend fun getAllNotesSimple(): ApiResult<List<NoteApiResponse>> {
        return try {
            val notes = notesService.getAllNotes()
            ApiResult.Success(notes)
        } catch (e: Exception) {
            ApiResult.Error(ApiError(code = 500, message = e.message ?: "Unknown error"))
        }
    }

    override suspend fun getNoteById(uuid: String): ApiResult<NoteApiResponse> {
        return try {
            val apiNote = notesService.getNoteById(uuid)
            ApiResult.Success(apiNote)
        } catch (e: Exception) {
            ApiResult.Error(ApiError(code = 404, message = e.message ?: "Note not found"))
        }
    }

    override suspend fun updateNote(note: NoteModel): ApiResult<NoteApiResponse> {
        return try {
            val response = notesService.updateNote(note.uuid!!, note.toNoteRequest())
            ApiResult.Success(response)
        } catch (e: Exception) {
            ApiResult.Error(ApiError(code = 500, message = e.message ?: "Update failed"))
        }
    }

    override suspend fun deleteNote(uuid: String): ApiResult<NoteApiResponse> {
        return try {
            val response = notesService.deleteNote(uuid)
            ApiResult.Success(response)
        } catch (e: Exception) {
            ApiResult.Error(ApiError(code = 500, message = e.message ?: "Delete failed"))
        }
    }
}