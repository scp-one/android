package com.greenknightlabs.scp_001.actions.enums

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ScpActionsType(val rawValue: String) {
    @SerialName("read")
    READ("read"),
    @SerialName("liked")
    LIKED("liked"),
    @SerialName("saved")
    SAVED("saved");

    fun displayName(): String {
        return when (this) {
            READ -> "Read"
            LIKED -> "Liked"
            SAVED -> "Bookmarked"
        }
    }

    companion object {
        fun allCases(): List<ScpActionsType> {
            return values().toList()
        }
    }
}