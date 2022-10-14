package com.greenknightlabs.scp_001.reports

import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface PostCommentReportsServiceApi {
    @POST("reports/posts/comments/{id}")
    suspend fun createPostCommentReport(
        @Header("Authorization") accessToken: String,
        @Path("id") id: String,
    )
}
