package com.greenknightlabs.scp_001.scps.models

import kotlinx.serialization.*
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonDecoder

@Serializable(with = ScpRefSerializer::class)
data class ScpRef(
    var isLoaded: Boolean,
    val id: String,
    val scp: Scp?
)

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = ScpRef::class)
object ScpRefSerializer : KSerializer<ScpRef> {
    override fun deserialize(decoder: Decoder): ScpRef {
        val idOrObject = (decoder as JsonDecoder).decodeJsonElement().toString()

        return try {
            val decodedScp = Json.decodeFromString<Scp>(idOrObject)
            ScpRef(true, decodedScp.id, decodedScp)
        } catch (e: Throwable) {
            ScpRef(false, idOrObject, null)
        }
    }
}