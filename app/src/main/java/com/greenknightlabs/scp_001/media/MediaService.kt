package com.greenknightlabs.scp_001.media

import android.util.Log
import com.greenknightlabs.scp_001.app.util.ApiErrorHandler
import com.greenknightlabs.scp_001.auth.AuthService
import com.greenknightlabs.scp_001.media.dtos.GetMediaFilterDto
import com.greenknightlabs.scp_001.media.dtos.asMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject
import okhttp3.MultipartBody
import timber.log.Timber
import java.io.File
import java.util.Timer
import javax.inject.Inject

class MediaService @Inject constructor(
    private val mediaServiceApi: MediaServiceApi,
    private val authService: AuthService,
    private val json: Json,
    private val apiErrorHandler: ApiErrorHandler
) {
    @Throws
    suspend fun uploadMedia(file: File) = withContext(Dispatchers.IO) {
//        val multipartImage =
//            MultipartBody.Part.createFormData("image", file.getName(), requestFile)

        val multiPartBody = MultipartBody.Part.createFormData("mediaFile", file.name)

        try {
            val accessToken = authService.getAccessTokenAsBearer()
            val media = mediaServiceApi.uploadMedia(accessToken, multiPartBody)
            media
        } catch (e: Throwable) {
            Timber.e(e)
            apiErrorHandler.throwApiError(e)
        }
    }

    @Throws
    suspend fun getMedia(filterDto:  GetMediaFilterDto) = withContext(Dispatchers.IO) {
        try {
            val accessToken = authService.getAccessTokenAsBearer()
            val queries = json.encodeToJsonElement(filterDto).jsonObject.toMap().mapValues { it.value.toString().replace("\"", "") }.filter { it.value != "null" }
            Timber.d(queries.toString())
            val media = mediaServiceApi.getMedia(accessToken, queries)
            media
        } catch (e: Throwable) {
            Timber.e(e)
            apiErrorHandler.throwApiError(e)
        }
    }

    @Throws
    suspend fun getMediaById(id: String) = withContext(Dispatchers.IO) {
        try {
            val accessToken = authService.getAccessTokenAsBearer()
            val media = mediaServiceApi.getMediaById(accessToken, id)
            media
        } catch (e: Throwable) {
            Timber.e(e)
            apiErrorHandler.throwApiError(e)
        }
    }

    @Throws
    suspend fun deleteMedia(id: String) = withContext(Dispatchers.IO) {
        try {
            val accessToken = authService.getAccessTokenAsBearer()
            mediaServiceApi.deleteMedia(accessToken, id)
        } catch (e: Throwable) {
            Timber.e(e)
            apiErrorHandler.throwApiError(e)
        }
    }
}