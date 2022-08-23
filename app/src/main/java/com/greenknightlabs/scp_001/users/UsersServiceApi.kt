package com.greenknightlabs.scp_001.users

import com.greenknightlabs.scp_001.users.dtos.EditUserDto
import com.greenknightlabs.scp_001.users.models.User
import retrofit2.Call
import retrofit2.http.*

interface UsersServiceApi {
    @GET("users")
    fun getUsers(
        @Header("Authorization") accessToken: String,
        @QueryMap filterDto: Map<String, String>
    ): Call<List<User>>

    @GET("users/{username}")
    fun getUserByUsername(
        @Header("Authorization") accessToken: String,
        @Path("username") username: String
    ): Call<User>

    @PATCH("users/{username}")
    fun editUser(
        @Header("Authorization") accessToken: String,
        @Path("username") username: String,
        @Body editUserDto: EditUserDto
    ): Call<User>
}