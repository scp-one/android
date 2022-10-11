package com.greenknightlabs.scp_001.actions

import com.greenknightlabs.scp_001.actions.dtos.CreateScpActionsDto
import com.greenknightlabs.scp_001.actions.dtos.GetScpActionsFilterDto
import com.greenknightlabs.scp_001.app.util.ApiErrorHandler
import com.greenknightlabs.scp_001.auth.AuthService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject
import timber.log.Timber
import javax.inject.Inject

class ScpActionsService @Inject constructor(
    private val scpActionsServiceApi: ScpActionsServiceApi,
    private val authService: AuthService,
    private val json: Json,
    private val apiErrorHandler: ApiErrorHandler
) {
    @Throws
    suspend fun createScpAction(id: String, createScpActionsDto: CreateScpActionsDto) = withContext(Dispatchers.IO) {
        try {
            val accessToken = authService.getAccessTokenAsBearer()
            scpActionsServiceApi.createScpAction(accessToken, id, createScpActionsDto)
        } catch (e: Throwable) {
            Timber.e(e)
            apiErrorHandler.throwApiError(e)
        }
    }

    @Throws
    suspend fun getScpActions(filterDto: GetScpActionsFilterDto) = withContext(Dispatchers.IO) {
        try {
            val accessToken = authService.getAccessTokenAsBearer()
            val queries = json.encodeToJsonElement(filterDto).jsonObject.toMap()
                .mapValues { it.value.toString().replace("\"", "") }
                .filter { it.value != "null" }
            val actions = scpActionsServiceApi.getScpActions(accessToken, queries)
            actions
        } catch (e: Throwable) {
            Timber.e(e)
            apiErrorHandler.throwApiError(e)
        }
    }
}
