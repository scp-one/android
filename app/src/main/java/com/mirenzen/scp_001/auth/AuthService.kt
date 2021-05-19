package com.mirenzen.scp_001.auth

import com.mirenzen.scp_001.app.util.ApiErrorHandler
import com.mirenzen.scp_001.auth.objects.AuthAccessInfo
import com.mirenzen.scp_001.auth.dtos.AuthCredentialsDto
import com.mirenzen.scp_001.auth.util.AuthMan
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.await
import timber.log.Timber
import javax.inject.Inject

class AuthService @Inject constructor(
    private val authServiceApi: AuthServiceApi,
    private val authMan: AuthMan,
    private val apiErrorHandler: ApiErrorHandler
) {
    suspend fun register(authCredentialsDto: AuthCredentialsDto): Result<AuthAccessInfo> = withContext(Dispatchers.IO) {
        try {
            when (val accessInfo = authServiceApi.register(authCredentialsDto).await()) {
                null -> Result.failure(Throwable("An error occurred during registration."))
                else -> Result.success(accessInfo)
            }
        } catch (e: Throwable) {
            Timber.e(e)
            apiErrorHandler.handleError(e)
        }
    }

    suspend fun login(authCredentialsDto: AuthCredentialsDto): Result<AuthAccessInfo> = withContext(Dispatchers.IO) {
        try {
            when (val accessInfo = authServiceApi.login(authCredentialsDto).await()) {
                null -> Result.failure(Throwable("An error occurred during login."))
                else -> Result.success(accessInfo)
            }
        } catch (e: Throwable) {
            Timber.e(e)
            apiErrorHandler.handleError(e)
        }
    }

    suspend fun logout(authAccessInfo: AuthAccessInfo): Result<Nothing?> = withContext(Dispatchers.IO) {
        try {
            val accessToken = "Bearer ${authAccessInfo.accessToken}"
            authServiceApi.logout(accessToken, authAccessInfo).await()
            Result.success(null)
        } catch (e: Throwable) {
            Timber.e(e)
            apiErrorHandler.handleError(e)
        }
    }

    private suspend inline fun refresh(authAccessInfo: AuthAccessInfo): Result<AuthAccessInfo> = withContext(Dispatchers.IO) {
        try {
            when (val newAccessInfo = authServiceApi.refresh(authAccessInfo).await()) {
                null -> Result.failure(Throwable("An error occurred during auth refresh."))
                else -> Result.success(newAccessInfo)
            }
        } catch (e: Throwable) {
            Timber.e(e)
            apiErrorHandler.handleError(e)
        }
    }

    suspend fun getVerifyEmailMail(email: String): Result<Nothing?> = withContext(Dispatchers.IO) {
        try {
            authServiceApi.getVerifyEmailMail(email).await()
            Result.success(null)
        } catch (e: Throwable) {
            Timber.e(e)
            apiErrorHandler.handleError(e)
        }
    }

    suspend fun getPasswordUpdateMail(email: String): Result<Nothing?> = withContext(Dispatchers.IO) {
        try {
            authServiceApi.getPasswordUpdateMail(email).await()
            Result.success(null)
        } catch (e: Throwable) {
            Timber.e(e)
            apiErrorHandler.handleError(e)
        }
    }

    suspend fun getAccessTokenAsBearer(): Result<String> = withContext(Dispatchers.IO) {
        val accessInfo = authMan.accessInfo
        val isExpired = authMan.isExpired()

        if (accessInfo == null || isExpired == null) {
            return@withContext Result.failure(Throwable("User is not logged in."))
        }

        if (isExpired == false) {
            return@withContext Result.success("Bearer ${accessInfo.accessToken}")
        }

        Timber.e("refreshing with $accessInfo")
        try {
            val result = refresh(accessInfo)
            val newAccessInfo = when (result.isFailure) {
                true -> throw result.exceptionOrNull()!!
                else -> result.getOrNull()!!
            }

            Timber.e("did refresh $newAccessInfo")
            authMan.didRefresh(newAccessInfo)
            Result.success("Bearer ${newAccessInfo.accessToken}")
        } catch (e: Throwable) {
            Timber.e(e)
            Result.failure(e)
        }
    }
}