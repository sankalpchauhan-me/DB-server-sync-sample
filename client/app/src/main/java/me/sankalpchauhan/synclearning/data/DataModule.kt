package me.sankalpchauhan.synclearning.data

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import me.sankalpchauhan.synclearning.data.db.DefaultLocalDataSource
import me.sankalpchauhan.synclearning.data.network.DefaultNetworkDataSource
import me.sankalpchauhan.synclearning.data.network.HeaderInterceptor
import okhttp3.Interceptor

@InstallIn(SingletonComponent::class)
@Module
abstract class DataModule {

    @Binds
    abstract fun bindHeaderInterceptor(interceptor: HeaderInterceptor): Interceptor

    @Binds
    abstract fun bindLocalDataSource(localDataSource: DefaultLocalDataSource): LocalDataSource

    @Binds
    abstract fun bindNetworkDataSource(networkDataSource: DefaultNetworkDataSource): NetworkDataSource
}