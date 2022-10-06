package com.greenknightlabs.scp_001.posts

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
class PostsModule {
    @Provides
    fun providePostsServiceApi(retrofit: Retrofit): PostsServiceApi {
        return retrofit.create(PostsServiceApi::class.java)
    }
}