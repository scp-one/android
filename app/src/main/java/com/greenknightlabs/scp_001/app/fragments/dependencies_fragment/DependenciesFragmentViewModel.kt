package com.greenknightlabs.scp_001.app.fragments.dependencies_fragment

import androidx.lifecycle.MutableLiveData
import com.greenknightlabs.scp_001.app.view_models.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DependenciesFragmentViewModel @Inject constructor() : BaseViewModel() {
    // properties
    val webViewUrl = MutableLiveData("")
    val shouldShowWebView = MutableLiveData(false)

    // functions
    fun handleOnTapDependencyComponent(url: String) {
        webViewUrl.value = url
        shouldShowWebView.value = true
    }
}