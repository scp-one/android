package com.greenknightlabs.scp_001.actions

import com.greenknightlabs.scp_001.actions.dtos.CreateScpActionsDto
import com.greenknightlabs.scp_001.actions.models.ScpActions
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.QueryMap

interface ScpActionsServiceApi {
    @POST("actions/scps")
    suspend fun createScpAction(
        @Header("Authorization") accessToken: String,
        @Body createScpActionDto: CreateScpActionsDto
    )

    @GET("actions/scps")
    suspend fun getScpActions(
        @Header("Authorization") accessToken: String,
        @QueryMap filterDto: Map<String, String>
    ): List<ScpActions>
}