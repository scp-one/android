package com.greenknightlabs.scp_001.scps.models

import com.greenknightlabs.scp_001.users.models.UserRef
import kotlinx.serialization.*
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.decodeFromJsonElement
import timber.log.Timber

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
        val json = Json {
            this.ignoreUnknownKeys = true
        }

        try {
            val id = decoder.decodeString()
            return ScpRef(false, id, null)
        } catch (e: Throwable) {
            // failed to decode
        }

        try {
            val scp = json.decodeFromString<Scp>((decoder as JsonDecoder).decodeJsonElement().toString())
            return ScpRef(true, scp.id, scp)
        } catch (e: Throwable) {
            // failed to decode
        }

        throw Error("Unable to decode ScpRef")
    }
}
