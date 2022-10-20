package com.greenknightlabs.scp_001.users.models

import kotlinx.serialization.*
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.json.*
import timber.log.Timber

@Serializable(with = UserRefSerializer::class)
data class UserRef(
    var isLoaded: Boolean,
    val id: String,
    var user: User?
)

// https://stackoverflow.com/questions/68195416/how-to-serialize-fields-with-varying-type

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = UserRef::class)
object UserRefSerializer : KSerializer<UserRef> {
    override fun deserialize(decoder: Decoder): UserRef {
        val json = Json {
            this.ignoreUnknownKeys = true
        }

        try {
            val id = decoder.decodeString()
            return UserRef(false, id, null)
        } catch (e: Throwable) {
            // failed to decode
        }

        try {
            val user = json.decodeFromString<User>((decoder as JsonDecoder).decodeJsonElement().toString())
            return UserRef(true, user.id, user)
        } catch (e: Throwable) {
            // failed to decode
        }

        throw Error("Unable to decode UserRef")
    }
}
