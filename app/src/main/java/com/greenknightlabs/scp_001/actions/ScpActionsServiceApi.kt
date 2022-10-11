package com.greenknightlabs.scp_001.actions

import com.greenknightlabs.scp_001.actions.dtos.CreateScpActionsDto
import com.greenknightlabs.scp_001.actions.models.ScpActions
import retrofit2.http.*

interface ScpActionsServiceApi {
    @POST("actions/scps/{id}")
    suspend fun createScpAction(
        @Header("Authorization") accessToken: String,
        @Path("id") id: String,
        @Body createScpActionDto: CreateScpActionsDto
    )

    @GET("actions/scps")
    suspend fun getScpActions(
        @Header("Authorization") accessToken: String,
        @QueryMap filterDto: Map<String, String>
    ): List<ScpActions>
}
