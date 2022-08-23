package com.greenknightlabs.scp_001.scps

import com.greenknightlabs.scp_001.scps.dtos.CreateScpDto
import com.greenknightlabs.scp_001.scps.dtos.EditScpDto
import com.greenknightlabs.scp_001.scps.models.Scp
import retrofit2.Call
import retrofit2.http.*

interface ScpsServiceApi {
    @POST("scps")
    fun createScp(
        @Header("Authorization") accessToken: String,
        @Body createScpDto: CreateScpDto
    ): Call<Scp?>

    @GET("scps")
    fun getScps(
        @Header("Authorization") accessToken: String,
        @QueryMap filterDto: Map<String, String>
    ): Call<List<Scp>?>

    @GET("scps/{id}")
    fun getScpById(
        @Header("Authorization") accessToken: String,
        @Path("id") id: String
    ): Call<Scp?>

    @PATCH("scps/{id}")
    fun editScp(
        @Header("Authorization") accessToken: String,
        @Path("id") id: String,
        @Body editScpDto: EditScpDto
    ): Call<Scp?>

    @DELETE("scps/{id}")
    fun deleteScp(
        @Header("Authorization") accessToken: String,
        @Path("id") id: String,
    ): Call<Void?>
}