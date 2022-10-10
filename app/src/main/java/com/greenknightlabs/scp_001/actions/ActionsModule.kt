package com.greenknightlabs.scp_001.actions

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
class ActionsModule {
    @Provides
    fun provideScpActionsServiceApi(retrofit: Retrofit): ScpActionsServiceApi {
        return retrofit.create(ScpActionsServiceApi::class.java)
    }

    @Provides
    fun providePostActionsServiceApi(retrofit: Retrofit): PostActionsServiceApi {
        return retrofit.create(PostActionsServiceApi::class.java)
    }
}