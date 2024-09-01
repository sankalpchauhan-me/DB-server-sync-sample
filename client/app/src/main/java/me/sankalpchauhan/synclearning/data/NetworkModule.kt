package me.sankalpchauhan.synclearning.data

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import me.sankalpchauhan.synclearning.data.network.HeaderInterceptor
import me.sankalpchauhan.synclearning.data.network.NetworkClient
import me.sankalpchauhan.synclearning.data.network.RetrofitClient
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(
        interceptor: HeaderInterceptor
    ): OkHttpClient {
        return NetworkClient(interceptor).getNetworkClient()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return RetrofitClient(okHttpClient).getRetrofit()
    }
}