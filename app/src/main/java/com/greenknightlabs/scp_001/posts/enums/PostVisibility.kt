package com.greenknightlabs.scp_001.posts.enums

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = PostVisibility.Companion::class)
enum class PostVisibility(val rawValue: Int) {
    @SerialName("1")
    HIDDEN(1),
    @SerialName("2")
    VISIBLE(2);

    companion object : KSerializer<PostVisibility> {
        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("PostVisibility", PrimitiveKind.INT)

        override fun serialize(encoder: Encoder, value: PostVisibility) {
            encoder.encodeInt(value.rawValue)
        }

        override fun deserialize(decoder: Decoder): PostVisibility {
            val decoderValue = decoder.decodeInt()
            return entries.first {
                it.rawValue == decoderValue
            }
        }
    }
}