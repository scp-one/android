package com.greenknightlabs.scp_001.reports

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
class ReportsModule {
    @Provides
    fun providePostReportsServiceApi(retrofit: Retrofit): PostReportsServiceApi {
        return retrofit.create(PostReportsServiceApi::class.java)
    }
}
