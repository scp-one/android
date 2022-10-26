package com.greenknightlabs.scp_001.comments

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
class CommentsModule {
    @Provides
    fun providePostCommentsServiceApi(retrofit: Retrofit): PostCommentsServiceApi {
        return retrofit.create(PostCommentsServiceApi::class.java)
    }
}
