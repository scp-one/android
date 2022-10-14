package com.greenknightlabs.scp_001.reports

import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface PostReportsServiceApi {
    @POST("reports/posts/{id}")
    suspend fun createPostReport(
        @Header("Authorization") accessToken: String,
        @Path("id") id: String,
    )
}
