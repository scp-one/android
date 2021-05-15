package com.mirenzen.scp_001.app.extensions

import androidx.appcompat.widget.AppCompatTextView
import androidx.core.text.PrecomputedTextCompat
import androidx.core.widget.TextViewCompat

fun AppCompatTextView.setTextAsync(text: String) {
    this.setTextFuture(
        PrecomputedTextCompat.getTextFuture(
            text,
            TextViewCompat.getTextMetricsParams(this),
            null
        )
    )
}