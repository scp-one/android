package com.greenknightlabs.scp_001.scps.fragments.scp_actions_fragment

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.ConcatAdapter
import com.greenknightlabs.scp_001.actions.ScpActionsService
import com.greenknightlabs.scp_001.actions.config.ActionsConstants
import com.greenknightlabs.scp_001.actions.dtos.CreateScpActionsDto
import com.greenknightlabs.scp_001.actions.dtos.GetScpActionsFilterDto
import com.greenknightlabs.scp_001.actions.enums.ScpActionsSortField
import com.greenknightlabs.scp_001.actions.enums.ScpActionsType
import com.greenknightlabs.scp_001.actions.models.ScpActions
import com.greenknightlabs.scp_001.app.adapters.PageAdapter
import com.greenknightlabs.scp_001.app.enums.PageState
import com.greenknightlabs.scp_001.app.extensions.makePopupMenu
import com.greenknightlabs.scp_001.app.util.NavMan
import com.greenknightlabs.scp_001.app.util.Queuey
import com.greenknightlabs.scp_001.auth.util.AuthMan
import com.greenknightlabs.scp_001.scps.enums.ScpSortOrder
import com.greenknightlabs.scp_001.scps.adapters.ScpsAdapter
import com.greenknightlabs.scp_001.scps.fragments.scp_fragment.ScpFragment
import com.greenknightlabs.scp_001.scps.models.Scp
import com.greenknightlabs.scp_001.scps.util.ScpSignaler
import com.greenknightlabs.scp_001.scps.view_models.ScpsViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import kotlin.concurrent.schedule

@HiltViewModel
class ScpActionsFragmentViewModel @Inject constructor(
    private val scpActionsService: ScpActionsService,
    private val authMan: AuthMan,
    private val navMan: NavMan,
    private val json: Json,
    private val scpSignaler: ScpSignaler,
    private val queuey: Queuey
) : ScpsViewModel(), ScpSignaler.Listener {
    // properties
    var itemsAdapter: ScpsAdapter? = null
    var pageAdapter: PageAdapter<Scp>? = null
    var adapter: ConcatAdapter? = null

    val actionType = MutableLiveData(ScpActionsType.SAVED)
    val sortOrder = MutableLiveData(ScpSortOrder.DESCENDING)

    val canRefresh = MutableLiveData(true)
    val isRefreshing = MutableLiveData(false)
    private val actionsList = MutableLiveData<MutableList<ScpActions>>(mutableListOf())

    // init
    init {
        scpSignaler.add(this)
        onRefreshAction()
    }

    override fun onCleared() {
        scpSignaler.remove(this)
        super.onCleared()
    }

    // functions
    fun onRefreshAction() {
        if (state.value == PageState.Fetching || canRefresh.value == false) {
            isRefreshing.value = false
            return
        }

        canRefresh.value = false
        isRefreshing.value = true
        paginate(true)
        Timer("refresh", false).schedule(5000) {
            viewModelScope.launch {
                canRefresh.value = true
            }
        }
    }

    override fun handleOnTapFailToLoad() {
        if (state.value != PageState.Fetching) {
            paginate(false)
        }
    }

    override fun paginate(refresh: Boolean) {
        state.value = PageState.Fetching

        viewModelScope.launch {
            val dto = provideFilterDto(refresh)

            try {
                val actions = scpActionsService.getScpActions(dto)
                val scps = actions.filter { it.scp.isLoaded }.map { it.scp.scp!! }

                if (refresh) {
                    actionsList.value?.clear()
                    items.value?.clear()
                    items.value?.addAll(scps)
                    itemsAdapter?.notifyDataSetChanged()
                } else if (actions.isNotEmpty()) {
                    val rangeStart = (items.value?.size ?: 0)
                    items.value?.addAll(scps)
                    itemsAdapter?.notifyItemRangeInserted(rangeStart, scps.size)
                }

                actionsList.value?.addAll(actions)

                state.value = when (actions.size < (dto.limit ?: ActionsConstants.ACTIONS_PAGE_SIZE)) {
                    true -> PageState.Bottom
                    else -> PageState.Idle
                }
                failedToLoad.value = false
                isRefreshing.value = false
            } catch (e: Throwable) {
                failedToLoad.value = true
                state.value = PageState.Bottom
                toastMessage.value = e.message
                isRefreshing.value = false
            }
        }
    }

    private fun provideFilterDto(refresh: Boolean): GetScpActionsFilterDto {
        val sortField = when (actionType.value!!) {
            ScpActionsType.READ -> ScpActionsSortField.READ_AT
            ScpActionsType.LIKED -> ScpActionsSortField.LIKED_AT
            ScpActionsType.SAVED -> ScpActionsSortField.SAVED_AT
        }

        var cursor: String? = null
        actionsList.value?.lastOrNull()?.let { lastAction ->
            try {
                val map = json.encodeToJsonElement(lastAction).jsonObject.toMap()
                map[sortField.rawValue]?.let { fieldValue ->
                    cursor = fieldValue.toString().replace("\"", "")
                }
            } catch (e: Throwable) {
                Timber.e(e)
            }
        }

        val sort = "${sortField.rawValue},${sortOrder.value?.rawValue}"

        return GetScpActionsFilterDto(
            null,
            authMan.payload?.id,
            null,
            actionType.value!!,
            sort,
            if (refresh) null else cursor,
            if (refresh) ActionsConstants.ACTIONS_PAGE_SIZE_REFRESH else ActionsConstants.ACTIONS_PAGE_SIZE
        )
    }

    fun handleOnTapMenuSort(view: View?) {
        val options = mutableListOf<Pair<String, () -> Unit>>()

        ScpActionsType.allCases().forEach {
            val name = if (it == actionType.value) { "${it.displayName()}*" } else { it.displayName() }
            options.add(Pair(name) {
                actionType.value = it
                didChangeSort()
            })
        }

        options.add(Pair("") {})

        ScpSortOrder.allCases().forEach {
            val name = if (it == sortOrder.value) { "${it.displayName()}*" } else { it.displayName() }
            options.add(Pair(name) {
                sortOrder.value = it
                didChangeSort()
            })
        }

        view?.makePopupMenu(options.map { it.first }) {
            options[it].second.invoke()
        }
    }

    private fun didChangeSort() {
        if (state.value == PageState.Fetching) return
        paginate(true)
    }

    override fun handleOnTapScp(scp: Scp) {
        val scpFragment = ScpFragment()
        scpFragment.scp = scp

        navMan.pushFragment(scpFragment)
    }

    override fun handleOnTapRead(scp: Scp) {
        scp.read = !scp.read
        didChangeScpAction(scp, ScpActionsType.READ, scp.read)
    }

    override fun handleOnTapLike(scp: Scp) {
        scp.liked = !scp.liked
        didChangeScpAction(scp, ScpActionsType.LIKED, scp.liked)
    }

    override fun handleOnTapSave(scp: Scp) {
        scp.saved = !scp.saved
        didChangeScpAction(scp, ScpActionsType.SAVED, scp.saved)
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

    override fun handleSignal(signal: ScpSignaler.ScpSignal) {
        when (signal) {
            is ScpSignaler.ScpSignal.ScpDidChange -> {
                items.value?.forEachIndexed { index, scp ->
                    if (scp.id == signal.scp.id) {
                        items.value?.set(index, signal.scp)
                        itemsAdapter?.notifyItemChanged(index)
                    }
                }
            }
        }
    }
}
