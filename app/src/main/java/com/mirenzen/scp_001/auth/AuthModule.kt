package com.mirenzen.scp_001.auth

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {
    @Provides
    fun provideAuthServiceInterface(retrofit: Retrofit): AuthServiceInterface {
        return retrofit.create(AuthServiceInterface::class.java)
    }
}