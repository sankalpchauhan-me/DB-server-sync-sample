package me.sankalpchauhan.synclearning.data.sync

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import javax.inject.Inject

@HiltWorker
class TriggerWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted private val workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        val uploadWorkRequest = OneTimeWorkRequestBuilder<UploadSyncWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        val downloadWorkRequest = OneTimeWorkRequestBuilder<DownloadSyncWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        Log.d("WorkManagerStatus", "Periodic Worker Triggered")

        WorkManager.getInstance(appContext)
            .beginWith(uploadWorkRequest)
            .then(downloadWorkRequest)
            .enqueue()

        WorkManager.getInstance(appContext).getWorkInfoByIdFlow(uploadWorkRequest.id).collect{workInfo->
            Log.d("WorkManagerStatus", "Immediate Work status: ${workInfo.state}")
        }
        WorkManager.getInstance(appContext).getWorkInfoByIdFlow(downloadWorkRequest.id).collect{workInfo->
            Log.d("WorkManagerStatus", "Immediate Work status: ${workInfo.state}")
        }

        return Result.success()
    }
}