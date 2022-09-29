package com.greenknightlabs.scp_001.auth

import com.greenknightlabs.scp_001.auth.objects.AuthAccessInfo
import com.greenknightlabs.scp_001.auth.dtos.AuthCredentialsDto
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface AuthServiceApi {
    @POST("auth/register")
    suspend fun register(
        @Body authCredentialsDto: AuthCredentialsDto
    ): AuthAccessInfo

    @POST("auth/login")
    suspend fun login(
        @Body authCredentialsDto: AuthCredentialsDto
    ): AuthAccessInfo

    @POST("auth/logout")
    suspend fun logout(
        @Body authAccessInfo: AuthAccessInfo
    )

    @POST("auth/refresh")
    suspend fun refresh(
        @Body authAccessInfo: AuthAccessInfo
    ): AuthAccessInfo

    @GET("auth/verify")
    suspend fun getVerifyEmailMail(
        @Query("email") email: String
    )

    @GET("auth/email")
    suspend fun getEmailUpdateMail(
        @Header("Authorization") accessToken: String,
        @Query("email") email: String
    )

    @GET("auth/password")
    suspend fun getPasswordUpdateMail(
        @Query("email") email: String
    )
}