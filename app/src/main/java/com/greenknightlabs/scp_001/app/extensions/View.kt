package com.greenknightlabs.scp_001.app.extensions

import android.view.View
import android.widget.PopupMenu

fun View.makePopupMenu(items: List<String>, onTapAction: (index: Int) -> Unit) {
    val menu = PopupMenu(this.context, this)

    items.forEachIndexed { index, item ->
        menu.menu.add(0, index, 0, item)
    }
    menu.setOnMenuItemClickListener { menuItem ->
        onTapAction(menuItem.itemId)
        true
    }

    menu.show()
}