package com.greenknightlabs.scp_001.actions.enums

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class PostActionsType(val rawValue: String) {
    @SerialName("liked")
    LIKED("liked");

    fun displayName(): String {
        return when (this) {
            LIKED -> "Liked"
        }
    }

    companion object {
        fun allCases(): List<PostActionsType> {
            return values().toList()
        }
    }
}