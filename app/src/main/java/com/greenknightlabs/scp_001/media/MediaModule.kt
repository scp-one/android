package com.greenknightlabs.scp_001.media

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
class MediaModule {
    @Provides
    fun provideMediaServiceApi(retrofit: Retrofit): MediaServiceApi {
        return retrofit.create(MediaServiceApi::class.java)
    }
}