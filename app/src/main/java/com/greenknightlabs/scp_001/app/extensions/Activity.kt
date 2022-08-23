package com.greenknightlabs.scp_001.app.extensions

import android.app.Activity
import android.net.Uri
import android.view.View
import androidx.browser.customtabs.CustomTabsIntent
import com.greenknightlabs.scp_001.R

fun Activity.getView(id: Int): View? {
    return this.findViewById(id) as? View
}

fun Activity.pushWebView(url: String) {
    val tab = CustomTabsIntent.Builder()
    tab.setToolbarColor(getColorFromAttr(R.attr.themeColorPrimary))
    tab.build().launchUrl(this, Uri.parse(url))
}