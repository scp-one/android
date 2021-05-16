package com.mirenzen.scp_001.auth.util

import android.content.Context
import android.content.SharedPreferences
import com.mirenzen.scp_001.R
import com.mirenzen.scp_001.auth.objects.AuthAccessInfo
import com.mirenzen.scp_001.auth.jwt.JwtPayload
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthMan @Inject constructor(
    private val context: Context,
    private val json: Json,
    private val jwtDecoder: JwtDecoder,
) {
    var accessInfo: AuthAccessInfo? = null
        private set
    var payload: JwtPayload? = null
        private set
    val isLoggedIn: Boolean
        get() = payload != null

    init {
        setAccessInfo(getCachedAccessInfo())
    }

    fun isExpired(): Boolean? {
        if (accessInfo == null) { return null }
        val payload = payload ?: return null
        return (payload.exp - (Date().time / 1000)) <= 0
    }

    fun didLogin(accessInfo: AuthAccessInfo) {
        setAccessInfo(accessInfo)
    }

    fun didRefresh(accessInfo: AuthAccessInfo) {
        setAccessInfo(accessInfo)
    }

    fun didLogout() {
        setAccessInfo(null)
    }

    private fun setAccessInfo(accessInfo: AuthAccessInfo?) {
        if (accessInfo != null) {
            this.payload = decodeJwtPayload(accessInfo.accessToken) ?: return
            this.accessInfo = accessInfo
            cacheAccessInfo(accessInfo)
        } else {
            this.payload = null
            this.accessInfo = null
            deleteCachedAccessInfo()
        }
    }

    private fun getCachedAccessInfo(): AuthAccessInfo? {
        val accessTokenKey = context.getString(R.string.key_auth_access_token)
        val refreshTokenKey = context.getString(R.string.key_auth_refresh_token)
        val sharedPrefs = getAuthAccessInfoSharedPref()

        val accessToken = sharedPrefs.getString(accessTokenKey, null) ?: return null
        val refreshToken = sharedPrefs.getString(refreshTokenKey, null) ?: return null

        return AuthAccessInfo(accessToken, refreshToken)
    }

    private fun cacheAccessInfo(accessInfo: AuthAccessInfo) {
        val accessTokenKey = context.getString(R.string.key_auth_access_token)
        val refreshTokenKey = context.getString(R.string.key_auth_refresh_token)
        val sharedPrefs = getAuthAccessInfoSharedPref()

        with(sharedPrefs.edit()) {
            putString(accessTokenKey, accessInfo.accessToken)
            if (accessInfo.refreshToken != null) {
                putString(refreshTokenKey, accessInfo.refreshToken)
            }
            apply()
        }
    }

    private fun deleteCachedAccessInfo() {
        val accessTokenKey = context.getString(R.string.key_auth_access_token)
        val refreshTokenKey = context.getString(R.string.key_auth_refresh_token)
        val sharedPrefs = getAuthAccessInfoSharedPref()

        with(sharedPrefs.edit()) {
            remove(accessTokenKey)
            remove(refreshTokenKey)
            apply()
        }
    }

    private fun getAuthAccessInfoSharedPref(): SharedPreferences {
        val fileName = context.getString(R.string.key_auth_access_info)
        return context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
    }

    private fun decodeJwtPayload(jwt: String): JwtPayload? {
        return try {
            json.decodeFromString<JwtPayload>(jwtDecoder.decode(jwt))
        } catch (e: Throwable) {
            Timber.e(e)
            null
        }
    }
}