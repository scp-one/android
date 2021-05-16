package com.mirenzen.scp_001.users

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
class UsersModule {
    @Provides
    fun provideUsersServiceApi(retrofit: Retrofit): UsersServiceApi {
        return retrofit.create(UsersServiceApi::class.java)
    }
}