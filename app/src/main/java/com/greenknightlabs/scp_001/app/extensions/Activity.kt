package com.greenknightlabs.scp_001.app.extensions

import android.app.Activity
import android.content.res.Resources
import android.net.Uri
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat


fun Activity.getView(id: Int): View? {
    return this.findViewById(id) as? View
}

fun Activity.pushWebView(url: String) {
    val tab = CustomTabsIntent.Builder()
//    tab.setToolbarColor(getColorFromAttr(R.attr.themeColorPrimary))
    tab.build().launchUrl(this, Uri.parse(url))
}

fun Activity.hideKeyboard() {
    this.currentFocus?.let {
        val imm = ContextCompat.getSystemService(this, InputMethodManager::class.java)
        imm?.hideSoftInputFromWindow(it.windowToken, 0)
    }
}

fun Activity.screenWidth(): Int {
    return Resources.getSystem().displayMetrics.widthPixels
}
