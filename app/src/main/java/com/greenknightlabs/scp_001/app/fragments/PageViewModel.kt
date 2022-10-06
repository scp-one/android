package com.greenknightlabs.scp_001.app.fragments

import androidx.lifecycle.MutableLiveData
import com.greenknightlabs.scp_001.app.fragments.base_fragment.BaseViewModel

abstract class PageViewModel<T> : BaseViewModel() {
    // properties
    val items = MutableLiveData<MutableList<T>>(mutableListOf())
    val failedToLoad = MutableLiveData(false)

    // functions
    abstract fun paginate(refresh: Boolean)
}