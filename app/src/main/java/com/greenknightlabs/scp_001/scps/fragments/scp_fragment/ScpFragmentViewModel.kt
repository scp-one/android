package com.greenknightlabs.scp_001.scps.fragments.scp_fragment

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.greenknightlabs.scp_001.actions.ScpActionsService
import com.greenknightlabs.scp_001.actions.dtos.CreateScpActionsDto
import com.greenknightlabs.scp_001.actions.enums.ScpActionsType
import com.greenknightlabs.scp_001.app.config.AppConstants
import com.greenknightlabs.scp_001.app.extensions.makePopupMenu
import com.greenknightlabs.scp_001.app.fragments.base_fragment.BaseViewModel
import com.greenknightlabs.scp_001.app.util.NavMan
import com.greenknightlabs.scp_001.app.util.Queuey
import com.greenknightlabs.scp_001.scps.models.Scp
import com.greenknightlabs.scp_001.scps.util.ScpSignaler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScpFragmentViewModel @Inject constructor(
    private val scpActionsService: ScpActionsService,
    private val scpSignaler: ScpSignaler,
    private val queuey: Queuey
) : BaseViewModel(), ScpSignaler.Listener {
    // properties
    val scp = MutableLiveData<Scp?>(null)
    val webViewUrl = MutableLiveData<String?>(null)
    val shouldShowWebView = MutableLiveData(false)

    // init
    init {
        scpSignaler.add(this)
    }

    override fun onCleared() {
        scpSignaler.remove(this)
        super.onCleared()
    }

    // functions
    fun handleOnTapMenuMore(view: View?) {
        val options = mutableListOf<Pair<String, () -> Unit>>()

        options.add(Pair("Open in wiki") {
            webViewUrl.value = scp.value?.sourceUrl
            shouldShowWebView.value = true
        })

        options.add(Pair("") {})

        val read = if (scp.value?.read == false) "Mark as read" else "Mark as unread"
        options.add(Pair(read) {
            scp.value?.let {
                it.read = !it.read
                didChangeScpAction(it, ScpActionsType.READ, it.read)
            }
        })

        val liked = if (scp.value?.liked == false) "Like" else "Unlike"
        options.add(Pair(liked) {
            scp.value?.let {
                it.liked = !it.liked
                didChangeScpAction(it, ScpActionsType.LIKED, it.liked)
            }
        })

        val saved = if (scp.value?.saved == false) "Bookmark" else "Remove bookmark"
        options.add(Pair(saved) {
            scp.value?.let {
                it.saved = !it.saved
                didChangeScpAction(it, ScpActionsType.SAVED, it.saved)
            }
        })

        view?.makePopupMenu(options.map { it.first }) {
            options[it].second.invoke()
        }
    }

    private fun didChangeScpAction(scp: Scp, actionType: ScpActionsType, newState: Boolean) {
        scpSignaler.send(ScpSignaler.ScpSignal.ScpDidChange(scp))

        queuey.queue({
            viewModelScope.launch {
                val dto = CreateScpActionsDto(actionType, newState)

                try {
                    scpActionsService.createScpAction(scp.id, dto)
                } catch (e: Throwable) {
                    toastMessage.value = e.message
                }
            }
        }, "${scp.id}${actionType.rawValue}")
    }

    fun handleOnTapLicense() {
        webViewUrl.value = scp.value?.sourceUrl
        shouldShowWebView.value = true
    }

    override fun handleSignal(signal: ScpSignaler.ScpSignal) {
        when (signal) {
            is ScpSignaler.ScpSignal.ScpDidChange -> {
                if (scp.value?.id == signal.scp.id) {
                    scp.value = signal.scp
                }
            }
        }
    }
}
