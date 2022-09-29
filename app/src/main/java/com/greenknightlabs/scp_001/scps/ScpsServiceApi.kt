package com.greenknightlabs.scp_001.scps

import com.greenknightlabs.scp_001.scps.dtos.CreateScpDto
import com.greenknightlabs.scp_001.scps.dtos.EditScpDto
import com.greenknightlabs.scp_001.scps.models.Scp
import retrofit2.Call
import retrofit2.http.*

interface ScpsServiceApi {
    @POST("scps")
    suspend fun createScp(
        @Header("Authorization") accessToken: String,
        @Body createScpDto: CreateScpDto
    ): Scp

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

    @PATCH("scps/{id}")
    suspend fun editScp(
        @Header("Authorization") accessToken: String,
        @Path("id") id: String,
        @Body editScpDto: EditScpDto
    ): Scp

    @DELETE("scps/{id}")
    suspend fun deleteScp(
        @Header("Authorization") accessToken: String,
        @Path("id") id: String,
    )
}