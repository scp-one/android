package com.greenknightlabs.scp_001.posts.enums

import android.provider.Telephony.Mms.Draft
import com.greenknightlabs.scp_001.scps.enums.ScpSortOrder
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import timber.log.Timber

@Serializable
enum class PostStatus(val rawValue: String) {
    @SerialName("Draft")
    DRAFT("Draft"),
    @SerialName("Pending")
    PENDING("Pending"),
    @SerialName("Approved")
    APPROVED("Approved"),
    @SerialName("Rejected")
    REJECTED("Rejected");

    fun value(): String {
        return this.rawValue
    }

    fun displayName(): String {
        return this.rawValue
    }

    companion object {
        fun allCases(): List<PostStatus> {
            return values().toList()
        }

        fun find(rawValue: String): PostStatus? {
            return allCases().firstOrNull { it.rawValue == rawValue }
        }
    }

//    @Serializer(PostStatus::class)
//    companion object : KSerializer<PostStatus> {
//        fun allCases(): List<PostStatus> {
//            return values().toList()
//        }
//
//        fun find(rawValue: String): PostStatus? {
//            return allCases().firstOrNull { it.rawValue == rawValue }
//        }
//
//        override val descriptor: SerialDescriptor
//            get() = PrimitiveSerialDescriptor("status", PrimitiveKind.STRING)
//
//        override fun serialize(encoder: Encoder, value: PostStatus) {
//            encoder.encodeString(value.rawValue)
//        }
//
//        override fun deserialize(decoder: Decoder): PostStatus {
////            val jsonString = (decoder as JsonDecoder).decodeJsonElement().toString()
////            Timber.d(jsonString)
//
//            val decoderValue = decoder.decodeString()
//            return values().first {
//                it.rawValue == decoderValue
//            }
//        }
//    }
}