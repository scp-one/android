package com.greenknightlabs.scp_001.posts.models

import com.greenknightlabs.scp_001.scps.models.Scp
import com.greenknightlabs.scp_001.scps.models.ScpRef
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonDecoder
import timber.log.Timber

@Serializable(with = PostRefSerializer::class)
data class PostRef(
    var isLoaded: Boolean,
    val id: String,
    val post: Post?
)

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = PostRef::class)
object PostRefSerializer : KSerializer<PostRef> {
    override fun deserialize(decoder: Decoder): PostRef {
        val json = Json {
            this.ignoreUnknownKeys = true
        }

        try {
            val id = decoder.decodeString()
            return PostRef(false, id, null)
        } catch (e: Throwable) {
            // failed to decode
        }

        try {
            val post = json.decodeFromString<Post>((decoder as JsonDecoder).decodeJsonElement().toString())
            return PostRef(true, post.id, post)
        } catch (e: Throwable) {
            // failed to decode
        }

        throw Error("Unable to decode PostRef")
    }
}
