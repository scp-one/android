package com.greenknightlabs.scp_001.posts

import com.greenknightlabs.scp_001.posts.dtos.CreatePostDto
import com.greenknightlabs.scp_001.posts.dtos.EditPostDto
import com.greenknightlabs.scp_001.posts.models.Post
import retrofit2.http.*

interface PostsServiceApi {
    @POST("posts")
    suspend fun createPost(
        @Header("Authorization") accessToken: String,
        @Body createPostDto: CreatePostDto
    ): Post

    @GET("posts")
    suspend fun getPosts(
        @Header("Authorization") accessToken: String,
        @QueryMap filterDto: Map<String, String>
    ): List<Post>

    @GET("posts/{id}")
    suspend fun getPostById(
        @Header("Authorization") accessToken: String,
        @Path("id") id: String,
    ): Post

    @PATCH("posts/{id}")
    suspend fun editPost(
        @Header("Authorization") accessToken: String,
        @Path("id") id: String,
        @Body editPostDto: EditPostDto
    ): Post

    @DELETE("posts/{id}")
    suspend fun deletePost(
        @Header("Authorization") accessToken: String,
        @Path("id") id: String,
    )
}