package com.greenknightlabs.scp_001.posts

import com.greenknightlabs.scp_001.app.util.ApiErrorHandler
import com.greenknightlabs.scp_001.auth.AuthService
import com.greenknightlabs.scp_001.posts.dtos.CreatePostDto
import com.greenknightlabs.scp_001.posts.dtos.EditPostDto
import com.greenknightlabs.scp_001.posts.dtos.GetPostsFilterDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject
import timber.log.Timber
import javax.inject.Inject

class PostsService @Inject constructor(
    private val postsServiceApi: PostsServiceApi,
    private val authService: AuthService,
    private val json: Json,
    private val apiErrorHandler: ApiErrorHandler
) {
    @Throws
    suspend fun createPost(createPostDto: CreatePostDto) = withContext(Dispatchers.IO) {
        try {
            val accessToken = authService.getAccessTokenAsBearer()
            val post = postsServiceApi.createPost(accessToken, createPostDto)
            post
        } catch (e: Throwable) {
            Timber.e(e)
            apiErrorHandler.throwApiError(e)
        }
    }

    @Throws
    suspend fun getPosts(filterDto: GetPostsFilterDto) = withContext(Dispatchers.IO) {
        try {
            val accessToken = authService.getAccessTokenAsBearer()
            val queries = json.encodeToJsonElement(filterDto).jsonObject.toMap()
                .mapValues { it.value.toString().replace("\"", "") }
                .filter { it.value != "null" }
            val posts = postsServiceApi.getPosts(accessToken, queries)
            posts
        } catch (e: Throwable) {
            Timber.e(e)
            apiErrorHandler.throwApiError(e)
        }
    }

    @Throws
    suspend fun getPostById(id: String) = withContext(Dispatchers.IO) {
        try {
            val accessToken = authService.getAccessTokenAsBearer()
            val post = postsServiceApi.getPostById(accessToken, id)
            post
        } catch (e: Throwable) {
            Timber.e(e)
            apiErrorHandler.throwApiError(e)
        }
    }

    @Throws
    suspend fun editPost(id: String, editPostDto: EditPostDto) = withContext(Dispatchers.IO) {
        try {
            val accessToken = authService.getAccessTokenAsBearer()
            val post = postsServiceApi.editPost(accessToken, id, editPostDto)
            post
        } catch (e: Throwable) {
            Timber.e(e)
            apiErrorHandler.throwApiError(e)
        }
    }

    @Throws
    suspend fun deletePost(id: String) = withContext(Dispatchers.IO) {
        try {
            val accessToken = authService.getAccessTokenAsBearer()
            postsServiceApi.deletePost(accessToken, id)
        } catch (e: Throwable) {
            Timber.e(e)
            apiErrorHandler.throwApiError(e)
        }
    }
}