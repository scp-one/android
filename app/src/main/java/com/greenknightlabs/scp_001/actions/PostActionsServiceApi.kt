package com.greenknightlabs.scp_001.actions

import com.greenknightlabs.scp_001.actions.dtos.CreatePostActionsDto
import com.greenknightlabs.scp_001.actions.models.PostActions
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface PostActionsServiceApi {
    @POST("actions/posts/{id}")
    suspend fun createPostAction(
        @Header("Authorization") accessToken: String,
        @Path("id") id: String,
        @Body createPostActionDto: CreatePostActionsDto
    )

    @GET("actions/posts")
    suspend fun getPostActions(
        @Header("Authorization") accessToken: String,
        @QueryMap filterDto: Map<String, String>
    ): List<PostActions>
}
