package com.mirenzen.scp_001.auth

import com.mirenzen.scp_001.auth.classes.AuthAccessInfo
import com.mirenzen.scp_001.auth.dtos.AuthCredentialsDto
import com.mirenzen.scp_001.auth.util.AuthMan
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.await
import timber.log.Timber
import javax.inject.Inject

class AuthService @Inject constructor(
    private val authServiceInterface: AuthServiceInterface,
    private val authMan: AuthMan
) {
    suspend fun register(authCredentialsDto: AuthCredentialsDto): Result<AuthAccessInfo> = withContext(Dispatchers.IO) {
        try {
            when (val accessInfo = authServiceInterface.register(authCredentialsDto).await()) {
                null -> Result.failure(Throwable("An error occurred during registration."))
                else -> Result.success(accessInfo)
            }
        } catch (e: Throwable) {
            Timber.e(e)
            Result.failure(e)
        }
    }

    suspend fun login(authCredentialsDto: AuthCredentialsDto): Result<AuthAccessInfo> = withContext(Dispatchers.IO) {
        try {
            when (val accessInfo = authServiceInterface.login(authCredentialsDto).await()) {
                null -> Result.failure(Throwable("An error occured during login."))
                else -> Result.success(accessInfo)
            }
        } catch (e: Throwable) {
            Timber.e(e)
            Result.failure(e)
        }
    }

    suspend fun logout(authAccessInfo: AuthAccessInfo): Result<Nothing?> = withContext(Dispatchers.IO) {
        try {
            val accessToken = "Bearer ${authAccessInfo.accessToken}"
            authServiceInterface.logout(accessToken, authAccessInfo).await()
            Result.success(null)
        } catch (e: Throwable) {
            Timber.e(e)
            Result.failure(e)
        }
    }
//
//    private fun refresh(
//        authAccessInfoDto: AuthAccessInfoDto,
//        completion: (AuthAccessInfoDto?, Error?) -> Unit
//    ) {
//        CoroutineScope(Dispatchers.IO).launch {
//            try {
//                val newAccessInfo = authService.refresh(authAccessInfoDto).await()
//                launch(Dispatchers.Main) {
//                    completion(newAccessInfo, null)
//                }
//            } catch (e: Throwable) {
//                launch(Dispatchers.Main) {
//                    Timber.e(e)
//                    completion(null, Error(e))
//                }
//            }
//        }
//    }
//
//    fun getEmailVerificationMail(
//        email: String,
//        completion: (Error?) -> Unit
//    ) {
//        CoroutineScope(Dispatchers.IO).launch {
//            try {
//                authService.getEmailVerificationMail(email).await()
//                launch(Dispatchers.Main) {
//                    completion(null)
//                }
//            } catch (e: Throwable) {
//                launch(Dispatchers.Main) {
//                    Timber.e(e)
//                    completion(Error(e))
//                }
//            }
//        }
//    }
//
//    fun getPasswordUpdateMail(
//        email: String,
//        completion: (Error?) -> Unit
//    ) {
//        CoroutineScope(Dispatchers.IO).launch {
//            try {
//                authService.getPasswordUpdateMail(email).await()
//                launch(Dispatchers.Main) {
//                    completion(null)
//                }
//            } catch (e: Throwable) {
//                launch(Dispatchers.Main) {
//                    Timber.e(e)
//                    completion(Error(e))
//                }
//            }
//        }
//    }
//
//    fun accessTokenAsBearer(completion: (String?, Error?) -> Unit) {
//        val isExpired = authMan.isExpired()
//        if (isExpired == false) {
//            return completion("Bearer " + authMan.accessInfo?.accessToken, null)
//        } else if (isExpired == null) {
//            return completion(null, Error())
//        }
//        val accessInfo = authMan.accessInfo ?: return completion(null, Error())
//
//        refresh(accessInfo) { newAccessInfo, error ->
//            when {
//                error != null -> completion(null, error)
//                newAccessInfo == null -> completion(null, null)
//                else -> {
//                    authMan.didRefresh(newAccessInfo)
//                    completion("Bearer " + newAccessInfo.accessToken, null)
//                }
//            }
//        }
//    }
}