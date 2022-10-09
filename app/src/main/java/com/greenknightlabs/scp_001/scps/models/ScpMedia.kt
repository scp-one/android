package com.greenknightlabs.scp_001.scps.models

import kotlinx.serialization.Serializable

@Serializable
data class ScpMedia(
    val url: String,
    val width: Int,
    val height: Int,
    val caption: String? = null
) {
    fun calculateHeight(viewWidth: Int, constrainHeight: Boolean = true): Int {
        if (viewWidth < 1) return 0

        val wRatio = this.width.toFloat() / viewWidth.toFloat()
        var height = this.height.toFloat() / wRatio
        if (constrainHeight && height > 1500f) { height = 1500f }

        return height.toInt()
    }
}