package me.sankalpchauhan.synclearning.data.sync

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import me.sankalpchauhan.synclearning.data.ApiResult
import me.sankalpchauhan.synclearning.data.LocalDataSource
import me.sankalpchauhan.synclearning.data.NetworkDataSource
import me.sankalpchauhan.synclearning.data.db.SyncStatus

@HiltWorker
class UploadSyncWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted private val workerParams: WorkerParameters,
    private val localDataSource: LocalDataSource,
    private val networkDataSource: NetworkDataSource
): CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        var toContinueLater = false
        Log.d("WorkManagerStatus", "Upload Sync Started")
        localDataSource.getSoftDeletedNotes().forEach { note->
            note.serverId?.let {
                val response = networkDataSource.deleteNote(note.serverId)
                when(response){
                    is ApiResult.Error -> {
                        Log.d("WorkManagerStatus", "Deleting DB API Error")
                    }
                    is ApiResult.Success -> {
                        Log.d("WorkManagerStatus", "Deleting DB note: ${note.uid}")
                        localDataSource.deleteNote(note.uid)
                    }
                }
            }
        }
        localDataSource.getAllNotesSimple().filter { isSyncRequired(it.syncStatus) }.forEach { note ->
                when (val response = networkDataSource.insertNote(note.toNoteModel())) {
                    is ApiResult.Error -> {
                        Log.d("WorkManagerStatus", "Api error")
                        localDataSource.updateNote(note.toNoteModel().copy(syncStatus = SyncStatus.FAILED))
                        toContinueLater = true
                    }
                    is ApiResult.Success -> {
                        Log.d("WorkManagerStatus", "Api success Updating with: ${note.toNoteModel().copy(uuid = response.data.serverId, syncStatus = SyncStatus.SUCCESS)}")
                        localDataSource.updateNote(note.toNoteModel().copy(uuid = response.data.serverId, syncStatus = SyncStatus.SUCCESS))
                    }
                }
        }

        return if (toContinueLater) Result.retry() else Result.success()
    }

    private fun isSyncRequired(syncStatus: SyncStatus): Boolean{
        return syncStatus!=SyncStatus.SUCCESS
    }

}