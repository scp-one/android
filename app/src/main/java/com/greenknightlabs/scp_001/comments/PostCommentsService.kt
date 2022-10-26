package com.greenknightlabs.scp_001.comments

import com.greenknightlabs.scp_001.app.util.ApiErrorHandler
import com.greenknightlabs.scp_001.auth.AuthService
import com.greenknightlabs.scp_001.comments.dtos.CreatePostCommentDto
import com.greenknightlabs.scp_001.comments.dtos.EditPostCommentDto
import com.greenknightlabs.scp_001.comments.dtos.GetPostCommentsFilterDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject
import timber.log.Timber
import javax.inject.Inject

class PostCommentsService @Inject constructor(
    private val postCommentsServiceApi: PostCommentsServiceApi,
    private val authService: AuthService,
    private val json: Json,
    private val apiErrorHandler: ApiErrorHandler
) {
    @Throws
    suspend fun createPostComment(postId: String, createPostCommentDto: CreatePostCommentDto) = withContext(Dispatchers.IO) {
        try {
            val accessToken = authService.getAccessTokenAsBearer()
            val postComment = postCommentsServiceApi.createPostComment(accessToken, postId, createPostCommentDto)
            postComment
        } catch (e: Throwable) {
            Timber.e(e)
            apiErrorHandler.throwApiError(e)
        }
    }

    @Throws
    suspend fun getPostComments(filterDto: GetPostCommentsFilterDto) = withContext(Dispatchers.IO) {
        try {
            val accessToken = authService.getAccessTokenAsBearer()
            val queries = json.encodeToJsonElement(filterDto).jsonObject.toMap()
                .mapValues { it.value.toString().replace("\"", "") }
                .filter { it.value != "null" }
            val postComments = postCommentsServiceApi.getPostComments(accessToken, queries)
            postComments
        } catch (e: Throwable) {
            Timber.e(e)
            apiErrorHandler.throwApiError(e)
        }
    }

    @Throws
    suspend fun getPostCommentById(id: String) = withContext(Dispatchers.IO) {
        try {
            val accessToken = authService.getAccessTokenAsBearer()
            val postComment = postCommentsServiceApi.getPostCommentById(accessToken, id)
            postComment
        } catch (e: Throwable) {
            Timber.e(e)
            apiErrorHandler.throwApiError(e)
        }
    }

    @Throws
    suspend fun editPostComment(id: String, editPostCommentDto: EditPostCommentDto) = withContext(Dispatchers.IO) {
        try {
            val accessToken = authService.getAccessTokenAsBearer()
            val postComment = postCommentsServiceApi.editPostComment(accessToken, id, editPostCommentDto)
            postComment
        } catch (e: Throwable) {
            Timber.e(e)
            apiErrorHandler.throwApiError(e)
        }
    }

    @Throws
    suspend fun deletePostComment(id: String) = withContext(Dispatchers.IO) {
        try {
            val accessToken = authService.getAccessTokenAsBearer()
            postCommentsServiceApi.deletePostComment(accessToken, id)
        } catch (e: Throwable) {
            Timber.e(e)
            apiErrorHandler.throwApiError(e)
        }
    }
}
