package com.mirenzen.scp_001.app.util

import com.mirenzen.scp_001.app.objects.ApiErrorBody
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject

class ApiErrorHandler @Inject constructor(
    private val json: Json
) {
    fun <T>handleError(e: Throwable): Result<T> {
        return when (val body = getApiErrorBody(e)) {
            null -> Result.failure(e)
            else -> Result.failure(Throwable(body.message.toString()))
        }
    }

    fun getApiErrorBody(e: Throwable): ApiErrorBody? {
        return if (e is HttpException) {
            try {
                val bodyString = e.response()?.errorBody()?.string() ?: ""
                json.decodeFromString<ApiErrorBody>(bodyString)
            } catch (e: Throwable) {
                Timber.e(e)
                null
            }
        } else {
            null
        }
    }
}