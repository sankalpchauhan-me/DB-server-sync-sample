package me.sankalpchauhan.synclearning.di

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import me.sankalpchauhan.synclearning.data.network.RetrofitClient
import me.sankalpchauhan.synclearning.data.db.AppDatabase
import me.sankalpchauhan.synclearning.data.sync.DefaultSyncWorkManager
import me.sankalpchauhan.synclearning.data.sync.SyncWorkManager
import retrofit2.Retrofit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindSyncWorkManager(syncWorkManager: DefaultSyncWorkManager): SyncWorkManager

    companion object{
        @Provides
        @Singleton
        fun provideSomeDependency(@ApplicationContext appContext: Context): AppDatabase {
            return AppDatabase.getInstance(appContext)
        }
    }

}