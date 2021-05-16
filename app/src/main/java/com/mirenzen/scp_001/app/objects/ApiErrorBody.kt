package com.mirenzen.scp_001.app.objects

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class ApiErrorBody(
    val statusCode: Int,
    val message: JsonElement
)