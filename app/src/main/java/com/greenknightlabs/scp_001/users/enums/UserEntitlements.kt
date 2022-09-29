package com.greenknightlabs.scp_001.users.enums

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
enum class UserEntitlements(val value: String) {
    Supporter("supporter"),
    Pro("pro"),
}