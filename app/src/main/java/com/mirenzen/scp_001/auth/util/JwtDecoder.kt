package com.mirenzen.scp_001.auth.util

import android.util.Base64
import javax.inject.Inject

class JwtDecoder @Inject constructor() {
    @Throws
    fun decode(jwt: String): String {
        val splitJwt = jwt.split(".")
        val encodedBody = splitJwt.getOrNull(1)
        val decodedBody = Base64.decode(encodedBody, Base64.URL_SAFE)
        return String(decodedBody, Charsets.UTF_8)
    }
}