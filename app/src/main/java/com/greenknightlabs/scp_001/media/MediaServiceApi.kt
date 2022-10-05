package com.greenknightlabs.scp_001.media

import com.greenknightlabs.scp_001.media.models.Media
import okhttp3.MultipartBody
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.QueryMap
import javax.inject.Inject

interface MediaServiceApi {
//    https://davidamunga.medium.com/working-with-multipart-form-data-using-retrofit-for-android-280307f23258

    @Multipart
    @POST("media")
    suspend fun uploadMedia(
        @Header("Authorization") accessToken: String,
        @Part file: MultipartBody.Part
    ): Media

    @GET("media")
    suspend fun getMedia(
        @Header("Authorization") accessToken: String,
        @QueryMap filterDto: Map<String, String>
    ): List<Media>

    @GET("media/{id}")
    suspend fun getMediaById(
        @Header("Authorization") accessToken: String,
        @Path("id") id: String,
    ): Media

    @DELETE("media/{id}")
    suspend fun deleteMedia(
        @Header("Authorization") accessToken: String,
        @Path("id") id: String,
    )
}
