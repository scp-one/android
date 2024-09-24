package com.greenknightlabs.scp_001.users.enums

import com.greenknightlabs.scp_001.scps.enums.ScpVisibility
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = UserAccessLevel.Companion::class)
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

    companion object : KSerializer<UserAccessLevel> {
        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("UserAccessLevel", PrimitiveKind.INT)

        override fun serialize(encoder: Encoder, value: UserAccessLevel) {
            encoder.encodeInt(value.value)
        }

        override fun deserialize(decoder: Decoder): UserAccessLevel {
            val decoderValue = decoder.decodeInt()
            return UserAccessLevel.entries.first {
                it.value == decoderValue
            }
        }
    }

//    @Serializer(UserAccessLevel::class)
//    companion object : KSerializer<UserAccessLevel> {
//        override val descriptor: SerialDescriptor
//            get() = TODO("Not yet implemented")
//
//        override fun serialize(encoder: Encoder, value: UserAccessLevel) {
//            encoder.encodeInt(value.value)
//        }
//
//        override fun deserialize(decoder: Decoder): UserAccessLevel {
//            val decoderValue = decoder.decodeInt()
//            return values().first {
//                it.value == decoderValue
//            }
//        }
//    }
}
