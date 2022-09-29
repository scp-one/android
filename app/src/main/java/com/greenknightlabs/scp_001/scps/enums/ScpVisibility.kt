package com.greenknightlabs.scp_001.scps.enums

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
enum class ScpVisibility(val value: Int) {
    HIDDEN(0),
    VISIBLE(1);

    @Serializer(ScpVisibility::class)
    companion object : KSerializer<ScpVisibility> {
        override val descriptor: SerialDescriptor
            get() = TODO("Not yet implemented")

        override fun serialize(encoder: Encoder, value: ScpVisibility) {
            encoder.encodeInt(value.value)
        }

        override fun deserialize(decoder: Decoder): ScpVisibility {
            val decoderValue = decoder.decodeInt()
            return values().first {
                it.value == decoderValue
            }
        }
    }
}