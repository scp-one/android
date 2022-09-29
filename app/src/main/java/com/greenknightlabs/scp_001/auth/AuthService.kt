package com.greenknightlabs.scp_001.auth

import com.greenknightlabs.scp_001.app.util.ApiErrorHandler
import com.greenknightlabs.scp_001.auth.objects.AuthAccessInfo
import com.greenknightlabs.scp_001.auth.dtos.AuthCredentialsDto
import com.greenknightlabs.scp_001.auth.util.AuthMan
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class AuthService @Inject constructor(
    private val authServiceApi: AuthServiceApi,
    private val authMan: AuthMan,
    private val apiErrorHandler: ApiErrorHandler
) {
    @Throws
    suspend fun register(authCredentialsDto: AuthCredentialsDto): AuthAccessInfo = withContext(Dispatchers.IO) {
        try {
            val accessInfo = authServiceApi.register(authCredentialsDto)
            accessInfo
        } catch (e: Throwable) {
            Timber.e(e)
            apiErrorHandler.throwApiError(e)
        }
    }

    @Throws
    suspend fun login(authCredentialsDto: AuthCredentialsDto): AuthAccessInfo = withContext(Dispatchers.IO) {
        try {
            val accessInfo = authServiceApi.login(authCredentialsDto)
            accessInfo
        } catch (e: Throwable) {
            Timber.e(e)
            apiErrorHandler.throwApiError(e)
        }
    }

    @Throws
    suspend fun logout(authAccessInfo: AuthAccessInfo) = withContext(Dispatchers.IO) {
        try {
            authServiceApi.logout(authAccessInfo)
        } catch (e: Throwable) {
            Timber.e(e)
            apiErrorHandler.throwApiError(e)
        }
    }

    @Throws
    private suspend inline fun refresh(authAccessInfo: AuthAccessInfo): AuthAccessInfo = withContext(Dispatchers.IO) {
        try {
            val newAccessInfo = authServiceApi.refresh(authAccessInfo)
            newAccessInfo
        } catch (e: Throwable) {
            Timber.e(e)
            apiErrorHandler.throwApiError(e)
        }
    }

    @Throws
    suspend fun getVerifyEmailMail(email: String) = withContext(Dispatchers.IO) {
        try {
            authServiceApi.getVerifyEmailMail(email)
        } catch (e: Throwable) {
            Timber.e(e)
            apiErrorHandler.throwApiError(e)
        }
    }

    @Throws
    suspend fun getEmailUpdateMail(email: String) = withContext(Dispatchers.IO) {
        try {
            val accessToken = getAccessTokenAsBearer()
            authServiceApi.getEmailUpdateMail(accessToken, email)
        } catch (e: Throwable) {
            Timber.e(e)
            apiErrorHandler.throwApiError(e)
        }
    }

    @Throws
    suspend fun getPasswordUpdateMail(email: String) = withContext(Dispatchers.IO) {
        try {
            authServiceApi.getPasswordUpdateMail(email)
        } catch (e: Throwable) {
            Timber.e(e)
            apiErrorHandler.throwApiError(e)
        }
    }

    @Throws
    suspend fun getAccessTokenAsBearer(): String = withContext(Dispatchers.IO) {
        val oldAccessInfo = authMan.accessInfo
        val isExpired = authMan.isExpired()

        if (oldAccessInfo == null || isExpired == null) {
            throw Throwable("User is not logged in.")
        }

        if (isExpired == false) {
            "Bearer ${oldAccessInfo.accessToken}"
        } else {
            val newAccessInfo = refresh(oldAccessInfo)
            authMan.didRefresh(newAccessInfo)
            "Bearer ${newAccessInfo.accessToken}"
        }
    }
}