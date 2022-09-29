package com.greenknightlabs.scp_001.app.view_models

import androidx.lifecycle.MutableLiveData
import com.greenknightlabs.scp_001.app.enums.PageState

abstract class PageViewModel<T> : BaseViewModel() {
    // properties
    val items = MutableLiveData<MutableList<T>>()

    // functions
    abstract fun paginate(refresh: Boolean)
}