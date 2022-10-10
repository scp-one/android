package com.greenknightlabs.scp_001.users.models

import kotlinx.serialization.*
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.json.*

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
        val idOrObject = (decoder as JsonDecoder).decodeJsonElement().toString()

        return try {
            val decodedUser = Json.decodeFromString<User>(idOrObject)
            UserRef(true, decodedUser.id, decodedUser)
        } catch (e: Throwable) {
            UserRef(false, idOrObject, null)
        }
    }
}