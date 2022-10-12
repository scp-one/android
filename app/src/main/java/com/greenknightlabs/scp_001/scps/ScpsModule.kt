package com.greenknightlabs.scp_001.scps

import com.greenknightlabs.scp_001.scps.util.ScpSignaler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
class ScpsModule {
    @Provides
    fun provideScpsServiceApi(retrofit: Retrofit): ScpsServiceApi {
        return retrofit.create(ScpsServiceApi::class.java)
    }
}
