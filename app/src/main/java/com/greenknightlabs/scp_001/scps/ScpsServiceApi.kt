package com.greenknightlabs.scp_001.scps

import com.greenknightlabs.scp_001.scps.models.Scp
import retrofit2.http.*

interface ScpsServiceApi {
    @GET("scps")
    suspend fun getScps(
        @Header("Authorization") accessToken: String,
        @QueryMap filterDto: Map<String, String>
    ): List<Scp>

    @GET("scps/{id}")
    suspend fun getScpById(
        @Header("Authorization") accessToken: String,
        @Path("id") id: String
    ): Scp
}