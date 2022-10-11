package com.greenknightlabs.scp_001.scps.fragments.scp_fragment

import androidx.lifecycle.MutableLiveData
import com.greenknightlabs.scp_001.app.fragments.base_fragment.BaseViewModel
import com.greenknightlabs.scp_001.app.util.NavMan
import com.greenknightlabs.scp_001.scps.models.Scp
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ScpFragmentViewModel @Inject constructor(
    private val navMan: NavMan
) : BaseViewModel() {
    // properties
    val scp = MutableLiveData<Scp?>(null)

    // functions

}
