package me.sankalpchauhan.synclearning.data.sync

import android.content.Context
import android.util.Log
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject

interface SyncWorkManager{
    fun initWorkManager()
}

class DefaultSyncWorkManager @Inject constructor(
    @ApplicationContext private val appContext: Context
): SyncWorkManager {

    override fun initWorkManager() {
        val uploadTriggerWorkRequest = PeriodicWorkRequestBuilder<TriggerWorker>(15, TimeUnit.MINUTES)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()
        Log.d("WorkManagerStatus", "Job ID: ${uploadTriggerWorkRequest.id}")
        WorkManager.getInstance(appContext)
            .enqueueUniquePeriodicWork("SyncWork8", ExistingPeriodicWorkPolicy.KEEP, uploadTriggerWorkRequest)


    }

}