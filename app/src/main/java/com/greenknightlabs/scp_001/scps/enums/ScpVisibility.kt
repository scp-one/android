package com.greenknightlabs.scp_001.scps.enums

import com.greenknightlabs.scp_001.posts.enums.PostVisibility
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = ScpVisibility.Companion::class)
enum class ScpVisibility(val rawValue: Int) {
    HIDDEN(1),
    VISIBLE(2);

    companion object : KSerializer<ScpVisibility> {
        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("ScpVisibility", PrimitiveKind.INT)

        override fun serialize(encoder: Encoder, value: ScpVisibility) {
            encoder.encodeInt(value.rawValue)
        }

        override fun deserialize(decoder: Decoder): ScpVisibility {
            val decoderValue = decoder.decodeInt()
            return ScpVisibility.entries.first {
                it.rawValue == decoderValue
            }
        }
    }

//    @Serializer(ScpVisibility::class)
//    companion object : KSerializer<ScpVisibility> {
//        override val descriptor: SerialDescriptor
//            get() = PrimitiveSerialDescriptor("ScpVisibility", PrimitiveKind.INT)
//
//        override fun serialize(encoder: Encoder, value: ScpVisibility) {
//            encoder.encodeInt(value.rawValue)
//        }
//
//        override fun deserialize(decoder: Decoder): ScpVisibility {
//            val decoderValue = decoder.decodeInt()
//            return values().first {
//                it.rawValue == decoderValue
//            }
//        }
//    }
}