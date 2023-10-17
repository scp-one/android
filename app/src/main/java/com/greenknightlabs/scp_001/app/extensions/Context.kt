package com.greenknightlabs.scp_001.app.extensions

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.util.TypedValue
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat

var toast: Toast? = null

fun Context.makeToast(message: String?) {
    val message = message ?: return
    if (message.isEmpty()) { return }

    if (toast != null) {
        toast?.cancel()
    }

    toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
    toast?.show()
}

fun Context.askConfirmation(onAccept: () -> Unit) {
    val builder = AlertDialog.Builder(this)
    builder.setMessage("Are you sure you want to do that?")
        .setPositiveButton("Yes") { _, _ -> onAccept() }
        .setNegativeButton("Cancel") { _, _ -> }
    builder.create().show()
}

fun Context.copyToClipboard(text: CharSequence) {
    (getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager)
        .setPrimaryClip(ClipData.newPlainText(null, text))
}

fun Context.hasPermission(permission: String): Boolean {
    val currentState = ContextCompat.checkSelfPermission(this, permission)
    return currentState == PackageManager.PERMISSION_GRANTED
}

@ColorInt
fun Context.getColorFromAttr(
    @AttrRes attrColor: Int,
    typedValue: TypedValue = TypedValue(),
    resolveRefs: Boolean = true
): Int {
    theme.resolveAttribute(attrColor, typedValue, resolveRefs)
    return typedValue.data
}
