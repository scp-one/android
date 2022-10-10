package com.greenknightlabs.scp_001.posts.models

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonDecoder

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
        val idOrObject = (decoder as JsonDecoder).decodeJsonElement().toString()

        return try {
            val decodedPost = Json.decodeFromString<Post>(idOrObject)
            PostRef(true, decodedPost.id, decodedPost)
        } catch (e: Throwable) {
            PostRef(false, idOrObject, null)
        }
    }
}