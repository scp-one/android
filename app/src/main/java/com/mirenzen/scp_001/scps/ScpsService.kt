package com.mirenzen.scp_001.scps

import com.mirenzen.scp_001.app.util.ApiErrorHandler
import com.mirenzen.scp_001.auth.AuthService
import com.mirenzen.scp_001.scps.models.Scp
import com.mirenzen.scp_001.users.dtos.GetUsersFilterDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
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
    suspend fun getScps(filterDto: GetUsersFilterDto): List<Scp>? = withContext(Dispatchers.IO) {
        try {
            val accessToken = authService.getAccessTokenAsBearer()
            val queries = json.decodeFromString<Map<String, String>>(filterDto.toString())
            val scps = scpsServiceApi.getScps(accessToken, queries).await()
            scps
        } catch (e: Throwable) {
            Timber.e(e)
            apiErrorHandler.throwApiError(e)
        }
    }
}