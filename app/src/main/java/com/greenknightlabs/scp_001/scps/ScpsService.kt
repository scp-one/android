package com.greenknightlabs.scp_001.scps

import com.greenknightlabs.scp_001.app.util.ApiErrorHandler
import com.greenknightlabs.scp_001.auth.AuthService
import com.greenknightlabs.scp_001.scps.dtos.GetScpsFilterDto
import com.greenknightlabs.scp_001.scps.models.Scp
import com.greenknightlabs.scp_001.users.dtos.GetUsersFilterDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject
import retrofit2.await
import timber.log.Timber
import javax.inject.Inject

class ScpsService @Inject constructor(
    private val scpsServiceApi: ScpsServiceApi,
    private val authService: AuthService,
    private val json: Json,
    private val apiErrorHandler: ApiErrorHandler
) {
    @Throws
    suspend fun getScps(filterDto: GetScpsFilterDto): List<Scp> = withContext(Dispatchers.IO) {
        try {
            val accessToken = authService.getAccessTokenAsBearer()
//            val queries = json.decodeFromString<Map<String, String>>(filterDto.toString())
            val queries = json.encodeToJsonElement(filterDto).jsonObject.toMap()
                .mapValues { it.value.toString().replace("\"", "") }
                .filter { it.value != "null" }
            val scps = scpsServiceApi.getScps(accessToken, queries)
            scps
        } catch (e: Throwable) {
            Timber.e(e)
            apiErrorHandler.throwApiError(e)
        }
    }
}