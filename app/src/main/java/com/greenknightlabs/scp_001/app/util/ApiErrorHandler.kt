package com.greenknightlabs.scp_001.app.util

import com.greenknightlabs.scp_001.app.objects.ApiErrorBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject

class ApiErrorHandler @Inject constructor(
    private val json: Json
) {
    @Throws
    suspend fun throwApiError(e: Throwable): Nothing = withContext(Dispatchers.Default) {
        val result = getApiErrorBody(e)
        when (result.isFailure) {
            true -> throw e
            else -> throw Throwable(result.getOrNull()?.message.toString())
        }
    }

    private suspend fun getApiErrorBody(e: Throwable): Result<ApiErrorBody> = withContext(Dispatchers.Default) {
        when (e) {
            is HttpException -> {
                try {
                    val bodyString = e.response()?.errorBody()?.byteStream()?.readBytes()?.decodeToString() ?: ""
                    Result.success(json.decodeFromString(bodyString))
                } catch(e: Throwable) {
                    Timber.e(e)
                    Result.failure(e)
                }
            }
            else -> Result.failure(e)
        }
    }
}