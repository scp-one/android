package com.greenknightlabs.scp_001.users.models

import androidx.annotation.StringDef
import com.greenknightlabs.scp_001.users.enums.UserAccessLevel
import kotlinx.serialization.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
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
        Timber.d("trying json")

        val json = (decoder as JsonDecoder).decodeJsonElement()
        Timber.d(json.toString())

        val jsonString = json.toString()
        Timber.d("parsing ${jsonString}")

        try {
            val decodedUser = Json.decodeFromString<User>(jsonString)
            Timber.d(decodedUser.toString())
            val ref = UserRef(true, decodedUser.id, decodedUser)
            Timber.d(ref.toString())
            return ref
        } catch (e: Throwable) {
            val ref = UserRef(false, jsonString, null)
            Timber.d(ref.toString())
            return ref
        }

//        return UserRef(true, decodedUser.id, decodedUser)


//        return try {
//
//        } catch (e: Exception) {
//            Timber.d("trying decoding string")
//
//            val id = (decoder as JsonDecoder).decodeJsonElement()
//            Timber.d(id.toString())
//
//            UserRef(false, id.toString(), null)
//        }
    }

    private fun parseInfo(json: JsonElement): UserRef {
//        val info = json["id"] ?: return emptyList()
        val jsonString = json.toString()
        Timber.d("parsing ${jsonString}")

        val decodedUser = Json.decodeFromString<User>(jsonString)
        Timber.d(decodedUser.toString())
        return UserRef(true, decodedUser.id, decodedUser)

//        return try {
//
//        } catch (e: Exception) {
//            val id = Json.decodeFromString<String>(jsonString)
//            UserRef(false, id, null)
////            (info as JsonArray).map { Json.decodeFromString<Name>(it.toString()) }
//        }
    }
}