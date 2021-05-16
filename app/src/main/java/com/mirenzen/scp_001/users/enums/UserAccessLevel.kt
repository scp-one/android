package com.mirenzen.scp_001.users.enums

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class UserAccessLevel(val value: Int) {
    @SerialName("0") UnverifiedUser(0),
    @SerialName("1") VerifiedUser(1),
    @SerialName("50") Moderator(50),
    @SerialName("99") Admin(99)
}