package com.mirenzen.scp_001.scps.models

import kotlinx.serialization.Serializable

@Serializable
data class Addendum(
    val name: String,
    val body: String
)