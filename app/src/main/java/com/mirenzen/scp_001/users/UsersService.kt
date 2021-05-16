package com.mirenzen.scp_001.users

import com.mirenzen.scp_001.app.util.ApiErrorHandler
import com.mirenzen.scp_001.auth.AuthService
import com.mirenzen.scp_001.users.dtos.EditUserDto
import com.mirenzen.scp_001.users.dtos.GetUsersFilterDto
import com.mirenzen.scp_001.users.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import retrofit2.await
import timber.log.Timber
import javax.inject.Inject

class UsersService @Inject constructor(
    private val usersServiceApi: UsersServiceApi,
    private val authService: AuthService,
    private val json: Json,
    private val apiErrorHandler: ApiErrorHandler
) {
    suspend fun getUsers(filterDto: GetUsersFilterDto): Result<List<User>> = withContext(Dispatchers.IO) {
        try {
            val result = authService.getAccessTokenAsBearer()
            val accessToken = when (result.isFailure) {
                true -> throw result.exceptionOrNull()!!
                else -> result.getOrNull()!!
            }

            val queries = json.decodeFromString<Map<String, String>>(filterDto.toString())
            when (val users = usersServiceApi.getUsers(accessToken, queries).await()) {
                null -> Result.failure(Throwable("An error occurred while getting users."))
                else -> Result.success(users)
            }
        } catch (e: Throwable) {
            Timber.e(e)
            apiErrorHandler.handleError(e)
        }
    }

    suspend fun getUserByUsername(username: String): Result<User> = withContext(Dispatchers.IO) {
        try {
            val result = authService.getAccessTokenAsBearer()
            val accessToken = when (result.isFailure) {
                true -> throw result.exceptionOrNull()!!
                else -> result.getOrNull()!!
            }

            when (val user = usersServiceApi.getUserByUsername(accessToken, username).await()) {
                null -> Result.failure(Throwable("An error occurred while getting user."))
                else -> Result.success(user)
            }
        } catch (e: Throwable) {
            Timber.e(e)
            apiErrorHandler.handleError(e)
        }
    }

    suspend fun editUser(
        username: String,
        editUserDto: EditUserDto
    ): Result<User> = withContext(Dispatchers.IO) {
        try {
            val result = authService.getAccessTokenAsBearer()
            val accessToken = when (result.isFailure) {
                true -> throw result.exceptionOrNull()!!
                else -> result.getOrNull()!!
            }

            when (val user = usersServiceApi.editUser(accessToken, username, editUserDto).await()) {
                null -> Result.failure(Throwable("An error occurred while editing user."))
                else -> Result.success(user)
            }
        } catch (e: Throwable) {
            Timber.e(e)
            apiErrorHandler.handleError(e)
        }
    }
}