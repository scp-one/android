package com.greenknightlabs.scp_001.users

import com.greenknightlabs.scp_001.users.dtos.EditUserDto
import com.greenknightlabs.scp_001.users.models.User
import retrofit2.Call
import retrofit2.http.*

interface UsersServiceApi {
    @GET("users")
    suspend fun getUsers(
        @Header("Authorization") accessToken: String,
        @QueryMap filterDto: Map<String, String>
    ): List<User>

    @GET("users/me")
    suspend fun getUserFromRequest(
        @Header("Authorization") accessToken: String,
    ): User

    @GET("users/{id}")
    suspend fun getUserById(
        @Header("Authorization") accessToken: String,
        @Path("id") id: String,
    ): User

    @PATCH("users/{id}")
    suspend fun editUserById(
        @Header("Authorization") accessToken: String,
        @Path("id") id: String,
        @Body editUserDto: EditUserDto
    ): User
}