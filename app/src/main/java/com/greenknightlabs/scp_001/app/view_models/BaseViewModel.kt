package com.greenknightlabs.scp_001.app.view_models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.greenknightlabs.scp_001.app.enums.PageState

abstract class BaseViewModel: ViewModel() {
    // properties
    val state = MutableLiveData(PageState.Idle)
    val toastMessage = MutableLiveData("")
}