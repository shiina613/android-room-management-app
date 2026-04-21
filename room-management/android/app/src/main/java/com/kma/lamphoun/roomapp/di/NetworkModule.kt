package com.kma.lamphoun.roomapp.di

import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.kma.lamphoun.roomapp.BuildConfig
import com.kma.lamphoun.roomapp.data.local.TokenDataStore
import com.kma.lamphoun.roomapp.data.remote.api.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideAuthInterceptor(tokenDataStore: TokenDataStore): Interceptor = Interceptor { chain ->
        val token = runBlocking { tokenDataStore.token.firstOrNull() }
        val request = if (!token.isNullOrBlank()) {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else chain.request()
        chain.proceed(request)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: Interceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        // Custom Gson: parse BigDecimal/Number → Double safely
        val gson = GsonBuilder()
            .setLenient()
            .registerTypeAdapter(Double::class.java, JsonDeserializer { json, _, _ ->
                json.asDouble
            })
            .registerTypeAdapter(Double::class.javaObjectType, JsonDeserializer { json, _, _ ->
                json.asDouble
            })
            .create()

        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)
}

