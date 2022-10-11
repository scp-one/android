package com.greenknightlabs.scp_001.scps.fragments.scp_actions_fragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.greenknightlabs.scp_001.actions.ScpActionsService
import com.greenknightlabs.scp_001.actions.config.ActionsConstants
import com.greenknightlabs.scp_001.actions.dtos.GetScpActionsFilterDto
import com.greenknightlabs.scp_001.actions.enums.ScpActionsSortField
import com.greenknightlabs.scp_001.actions.enums.ScpActionsType
import com.greenknightlabs.scp_001.actions.models.ScpActions
import com.greenknightlabs.scp_001.app.enums.PageState
import com.greenknightlabs.scp_001.app.util.NavMan
import com.greenknightlabs.scp_001.auth.util.AuthMan
import com.greenknightlabs.scp_001.scps.enums.ScpSortOrder
import com.greenknightlabs.scp_001.scps.adapters.ScpsAdapter
import com.greenknightlabs.scp_001.scps.fragments.scp_fragment.ScpFragment
import com.greenknightlabs.scp_001.scps.models.Scp
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
    private val json: Json
) : ScpsViewModel() {
    // properties
    var adapter: ScpsAdapter? = null

    val actionType = MutableLiveData(ScpActionsType.LIKED)
    val sortOrder = MutableLiveData(ScpSortOrder.DESCENDING)
    val series = MutableLiveData<Int?>(null)

    val canRefresh = MutableLiveData(true)
    val isRefreshing = MutableLiveData(false)
    private val actionsList = MutableLiveData<MutableList<ScpActions>>(mutableListOf())

    // init
    init {
        onRefreshAction()
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
                    adapter?.notifyDataSetChanged()
                } else if (actions.isNotEmpty()) {
                    items.value?.addAll(scps)
                    adapter?.notifyItemInserted(items.value!!.size)
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

    override fun handleOnTapScp(scp: Scp) {
        val scpFragment = ScpFragment()
        scpFragment.scp = scp

        navMan.pushFragment(scpFragment)
    }

    override fun handleOnTapRead(scp: Scp) {
        TODO("Not yet implemented")
    }

    override fun handleOnTapLike(scp: Scp) {
        TODO("Not yet implemented")
    }

    override fun handleOnTapSave(scp: Scp) {
        TODO("Not yet implemented")
    }
}
