package com.greenknightlabs.scp_001.users.enums

import kotlinx.serialization.Serializable

@Serializable
enum class UserEntitlements(val rawValue: String) {
    Supporter("supporter"),
    Pro("pro");
}
