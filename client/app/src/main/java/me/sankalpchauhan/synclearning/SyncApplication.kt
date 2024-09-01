package me.sankalpchauhan.synclearning

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.WorkManager
import dagger.hilt.android.HiltAndroidApp
import me.sankalpchauhan.synclearning.data.sync.SyncWorkManager
import javax.inject.Inject

@HiltAndroidApp
class SyncApplication: Application(), Configuration.Provider {

    @Inject
    lateinit var syncWorkManager: SyncWorkManager

    @Inject
    lateinit var workerFactory: HiltWorkerFactory


    override fun onCreate() {
        super.onCreate()
        syncWorkManager.initWorkManager()
    }


    override val workManagerConfiguration: Configuration by lazy {
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }
}