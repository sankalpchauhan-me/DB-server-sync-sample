package me.sankalpchauhan.synclearning.data.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import javax.inject.Inject


class NetworkClient @Inject constructor(
    private val interceptor: Interceptor
) {

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()
    }

    fun getNetworkClient(): OkHttpClient {
        return client
    }
}