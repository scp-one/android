package com.greenknightlabs.scp_001.posts.enums

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
enum class PostVisibility(val rawValue: Int) {
    HIDDEN(1),
    VISIBLE(2);

    @Serializer(PostVisibility::class)
    companion object : KSerializer<PostVisibility> {
        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("test", PrimitiveKind.INT)

        override fun serialize(encoder: Encoder, value: PostVisibility) {
            encoder.encodeInt(value.rawValue)
        }

        override fun deserialize(decoder: Decoder): PostVisibility {
            val decoderValue = decoder.decodeInt()
            return values().first {
                it.rawValue == decoderValue
            }
        }
    }
}