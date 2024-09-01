package me.sankalpchauhan.synclearning.data.sync

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import me.sankalpchauhan.synclearning.data.ApiResult
import me.sankalpchauhan.synclearning.data.LocalDataSource
import me.sankalpchauhan.synclearning.data.NetworkDataSource
import me.sankalpchauhan.synclearning.data.NoteApiResponse
import me.sankalpchauhan.synclearning.data.NoteModel
import me.sankalpchauhan.synclearning.data.db.NotesEntity
import me.sankalpchauhan.synclearning.data.db.SyncStatus
import javax.inject.Inject

@HiltWorker
class DownloadSyncWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted private val workerParams: WorkerParameters,
    private val localDataSource: LocalDataSource,
    private val networkDataSource: NetworkDataSource
): CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        Log.d("WorkManagerStatus", "Download Sync Started")
        var toContinueLater = false
        when(val response = networkDataSource.getAllNotesSimple()){
            is ApiResult.Error -> {
                toContinueLater = true
                Log.d("WorkManagerStatus", "Api Error")
            }
            is ApiResult.Success -> {
                val notesList = response.data
                notesList.forEach{ apiNote->
                        val localNote = localDataSource.getNoteByUUID(apiNote.serverId)
                        if(localNote==null){
                            Log.d("WorkManagerStatus", "Api Success: Inserting ${apiNote.toNoteModel()}")
                            localDataSource.insertNote(apiNote.toNoteModel())
                        } else{
                            Log.d("WorkManagerStatus", "Api Success: Resolving Conflict and updating ${resolveConflict(localNote, apiNote).copy(syncStatus = SyncStatus.SUCCESS)}")
                            localDataSource.updateNote(resolveConflict(localNote, apiNote).copy(syncStatus = SyncStatus.SUCCESS))
                    }

                }
            }
        }
        return if (toContinueLater) Result.retry() else Result.success()
    }

    private fun resolveConflict(localNote: NotesEntity, serverNote: NoteApiResponse): NoteModel {
        return when {
            localNote.version > serverNote.version -> localNote.toNoteModel()
            localNote.version < serverNote.version -> serverNote.toNoteModel()
            localNote.lastModified > serverNote.lastModified -> localNote.toNoteModel()
            localNote.lastModified < serverNote.lastModified -> serverNote.toNoteModel()
            else -> serverNote.toNoteModel()
        }
    }
}