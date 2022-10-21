package com.greenknightlabs.scp_001.users.enums

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
enum class UserAccessLevel(val value: Int) {
    UnverifiedUser(1),
    VerifiedUser(2),
    Moderator(50),
    Admin(99);

    fun displayName(): String {
        return when (this) {
            UnverifiedUser -> "Unverified"
            VerifiedUser -> "Verified"
            Moderator -> "Moderator"
            Admin -> "Admin"
        }
    }

    @Serializer(UserAccessLevel::class)
    companion object : KSerializer<UserAccessLevel> {
        override val descriptor: SerialDescriptor
            get() = TODO("Not yet implemented")

        override fun serialize(encoder: Encoder, value: UserAccessLevel) {
            encoder.encodeInt(value.value)
        }

        override fun deserialize(decoder: Decoder): UserAccessLevel {
            val decoderValue = decoder.decodeInt()
            return values().first {
                it.value == decoderValue
            }
        }
    }
}
