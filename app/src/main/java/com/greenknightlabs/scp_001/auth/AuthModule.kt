package com.greenknightlabs.scp_001.auth

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {
    @Provides
    fun provideAuthServiceApi(retrofit: Retrofit): AuthServiceApi {
        return retrofit.create(AuthServiceApi::class.java)
    }
}