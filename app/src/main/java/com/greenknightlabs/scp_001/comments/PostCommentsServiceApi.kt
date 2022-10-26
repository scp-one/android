package com.greenknightlabs.scp_001.comments

import com.greenknightlabs.scp_001.comments.dtos.CreatePostCommentDto
import com.greenknightlabs.scp_001.comments.dtos.EditPostCommentDto
import com.greenknightlabs.scp_001.comments.models.PostComment
import retrofit2.http.*

interface PostCommentsServiceApi {
    @POST("comments/posts/{postId}")
    suspend fun createPostComment(
        @Header("Authorization") accessToken: String,
        @Path("postId") postId: String,
        @Body createPostCommentDto: CreatePostCommentDto
    ): PostComment

    @GET("comments/posts")
    suspend fun getPostComments(
        @Header("Authorization") accessToken: String,
        @QueryMap filterDto: Map<String, String>
    ): List<PostComment>

    @GET("comments/posts/{id}")
    suspend fun getPostCommentById(
        @Header("Authorization") accessToken: String,
        @Path("id") id: String
    ): PostComment

    @PATCH("comments/posts/{id}")
    suspend fun editPostComment(
        @Header("Authorization") accessToken: String,
        @Path("id") id: String,
        @Body editPostCommentDto: EditPostCommentDto
    ): PostComment

    @DELETE("comments/posts/{id}")
    suspend fun deletePostComment(
        @Header("Authorization") accessToken: String,
        @Path("id") id: String,
    )
}
