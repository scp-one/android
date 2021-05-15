package com.mirenzen.scp_001.app

import android.content.Context
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.mirenzen.scp_001.app.config.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonBuilder
import kotlinx.serialization.json.JsonConfiguration
import okhttp3.MediaType
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    fun provideAppContext(): Context {
        return App.context
    }

    @Provides
    fun provideJson(): Json {
        return Json {
            ignoreUnknownKeys = true
        }
    }

    @Provides
    fun provideRetrofit(json: Json): Retrofit {
        val contentType = MediaType.parse("application/json")!!
        return Retrofit.Builder()
            .baseUrl(Constants.API_URL)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }
}