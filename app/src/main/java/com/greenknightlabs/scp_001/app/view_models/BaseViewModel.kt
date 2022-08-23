package com.greenknightlabs.scp_001.app.view_models

import androidx.lifecycle.ViewModel
import com.greenknightlabs.scp_001.app.enums.PageState

abstract class BaseViewModel<T> : ViewModel() {
    val items = mutableListOf<T>()
    var state = PageState.Idle
        protected set

    abstract suspend fun paginate(refresh: Boolean): Result<Nothing?>
}