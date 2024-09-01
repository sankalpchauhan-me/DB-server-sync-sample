package me.sankalpchauhan.synclearning.data.network

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Inject

class RetrofitClient @Inject constructor(
    private val okHttpClient: OkHttpClient
) {
    @Volatile
    var instance: Retrofit? = null

    fun getRetrofit(): Retrofit{
        val networkJson = Json { ignoreUnknownKeys = true }
        return instance?: synchronized(this){
            val retrofit = Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")
                .client(okHttpClient)
                .addConverterFactory(
                    networkJson.asConverterFactory("application/json".toMediaType())
                )
                .build()
            instance = retrofit
            retrofit
        }
    }

}