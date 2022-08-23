package com.greenknightlabs.scp_001.users

import com.greenknightlabs.scp_001.app.util.ApiErrorHandler
import com.greenknightlabs.scp_001.auth.AuthService
import com.greenknightlabs.scp_001.users.dtos.EditUserDto
import com.greenknightlabs.scp_001.users.dtos.GetUsersFilterDto
import com.greenknightlabs.scp_001.users.models.User
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
    @Throws
    suspend fun getUsers(filterDto: GetUsersFilterDto): List<User> = withContext(Dispatchers.IO) {
        try {
            val accessToken = authService.getAccessTokenAsBearer()
            val queries = json.decodeFromString<Map<String, String>>(filterDto.toString())
            val users = usersServiceApi.getUsers(accessToken, queries).await()
            users
        } catch (e: Throwable) {
            Timber.e(e)
            apiErrorHandler.throwApiError(e)
        }
    }

    @Throws
    suspend fun getUserByUsername(username: String): User = withContext(Dispatchers.IO) {
        try {
            val accessToken = authService.getAccessTokenAsBearer()
            val user = usersServiceApi.getUserByUsername(accessToken, username).await()
            user
        } catch (e: Throwable) {
            Timber.e(e)
            apiErrorHandler.throwApiError(e)
        }
    }

    @Throws
    suspend fun editUser(username: String, editUserDto: EditUserDto): User = withContext(Dispatchers.IO) {
        try {
            val accessToken = authService.getAccessTokenAsBearer()
            val user = usersServiceApi.editUser(accessToken, username, editUserDto).await()
            user
        } catch (e: Throwable) {
            Timber.e(e)
            apiErrorHandler.throwApiError(e)
        }
    }
}