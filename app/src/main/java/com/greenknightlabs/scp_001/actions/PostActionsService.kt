package com.greenknightlabs.scp_001.actions

import com.greenknightlabs.scp_001.actions.dtos.CreatePostActionsDto
import com.greenknightlabs.scp_001.actions.dtos.GetPostActionsFilterDto
import com.greenknightlabs.scp_001.app.util.ApiErrorHandler
import com.greenknightlabs.scp_001.auth.AuthService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject
import timber.log.Timber
import javax.inject.Inject

class PostActionsService @Inject constructor(
    private val postActionsServiceApi: PostActionsServiceApi,
    private val authService: AuthService,
    private val json: Json,
    private val apiErrorHandler: ApiErrorHandler
) {
    @Throws
    suspend fun createPostAction(id: String, createPostActionsDto: CreatePostActionsDto) = withContext(Dispatchers.IO) {
        try {
            val accessToken = authService.getAccessTokenAsBearer()
            postActionsServiceApi.createPostAction(accessToken, id, createPostActionsDto)
        } catch (e: Throwable) {
            Timber.e(e)
            apiErrorHandler.throwApiError(e)
        }
    }

    @Throws
    suspend fun getPostActions(filterDto: GetPostActionsFilterDto) = withContext(Dispatchers.IO) {
        try {
            val accessToken = authService.getAccessTokenAsBearer()
            val queries = json.encodeToJsonElement(filterDto).jsonObject.toMap()
                .mapValues { it.value.toString().replace("\"", "") }
                .filter { it.value != "null" }
            val actions = postActionsServiceApi.getPostActions(accessToken, queries)
            actions
        } catch (e: Throwable) {
            Timber.e(e)
            apiErrorHandler.throwApiError(e)
        }
    }
}
