package com.mirenzen.scp_001.auth

import com.mirenzen.scp_001.auth.objects.AuthAccessInfo
import com.mirenzen.scp_001.auth.dtos.AuthCredentialsDto
import retrofit2.Call
import retrofit2.http.*

interface AuthServiceApi {
    @POST("auth/register")
    fun register(
        @Body authCredentialsDto: AuthCredentialsDto
    ): Call<AuthAccessInfo?>

    @POST("auth/login")
    fun login(
        @Body authCredentialsDto: AuthCredentialsDto
    ): Call<AuthAccessInfo?>

    @POST("auth/logout")
    fun logout(
        @Header("Authorization") accessToken: String,
        @Body authAccessInfo: AuthAccessInfo
    ): Call<Void?>

    @POST("auth/refresh")
    fun refresh(
        @Body authAccessInfo: AuthAccessInfo
    ): Call<AuthAccessInfo?>

    @GET("auth/verify")
    fun getVerifyEmailMail(
        @Query("email") email: String
    ): Call<Void?>

    @GET("auth/email")
    fun getEmailUpdateMail(
        @Header("Authorization") accessToken: String,
        @Query("email") email: String
    ): Call<Void?>

    @GET("auth/password")
    fun getPasswordUpdateMail(
        @Query("email") email: String
    ): Call<Void?>
}