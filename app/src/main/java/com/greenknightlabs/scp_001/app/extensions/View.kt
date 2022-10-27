package com.greenknightlabs.scp_001.app.extensions

import android.content.Context
import android.os.Build
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.PopupMenu

fun View.makePopupMenu(items: List<String>, onTapAction: (index: Int) -> Unit) {
    val menu = PopupMenu(this.context, this)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        menu.menu.setGroupDividerEnabled(true)
    }

    var group = 0
    items.forEachIndexed { index, item ->
        if (item.isEmpty()) {
            group += 1
        } else {
            menu.menu.add(group, index, 0, item)
        }
    }
    menu.setOnMenuItemClickListener { menuItem ->
        onTapAction(menuItem.itemId)
        true
    }

    menu.show()
}

fun View.showKeyboard() {
    this.requestFocus()
    val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}
