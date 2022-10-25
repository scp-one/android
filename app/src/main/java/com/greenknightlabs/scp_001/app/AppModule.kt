package com.greenknightlabs.scp_001.app

import android.content.Context
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.greenknightlabs.scp_001.app.config.AppConstants
import com.greenknightlabs.scp_001.app.util.ApiErrorHandler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
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
    fun provideApiErrorHandler(json: Json): ApiErrorHandler {
        return ApiErrorHandler(json)
    }

    @Provides
    fun provideRetrofit(json: Json): Retrofit {
        val contentType = "application/json".toMediaTypeOrNull()!!
        return Retrofit.Builder()
            .baseUrl(AppConstants.API_URL)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }
}
