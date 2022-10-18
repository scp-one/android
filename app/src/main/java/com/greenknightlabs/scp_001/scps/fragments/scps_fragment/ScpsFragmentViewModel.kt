package com.greenknightlabs.scp_001.scps.fragments.scps_fragment

import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.greenknightlabs.scp_001.actions.ScpActionsService
import com.greenknightlabs.scp_001.actions.dtos.CreateScpActionsDto
import com.greenknightlabs.scp_001.actions.enums.ScpActionsType
import com.greenknightlabs.scp_001.app.enums.PageState
import com.greenknightlabs.scp_001.app.extensions.makePopupMenu
import com.greenknightlabs.scp_001.app.util.NavMan
import com.greenknightlabs.scp_001.app.util.Preferences
import com.greenknightlabs.scp_001.app.util.Queuey
import com.greenknightlabs.scp_001.auth.util.AuthMan
import com.greenknightlabs.scp_001.scps.ScpsService
import com.greenknightlabs.scp_001.scps.config.ScpsConstants
import com.greenknightlabs.scp_001.scps.dtos.GetScpsFilterDto
import com.greenknightlabs.scp_001.scps.enums.ScpStatus
import com.greenknightlabs.scp_001.scps.enums.ScpVisibility
import com.greenknightlabs.scp_001.scps.adapters.ScpsAdapter
import com.greenknightlabs.scp_001.scps.enums.ScpSortField
import com.greenknightlabs.scp_001.scps.enums.ScpSortOrder
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
class ScpsFragmentViewModel @Inject constructor(
    private val scpsService: ScpsService,
    private val scpActionsService: ScpActionsService,
    private val preferences: Preferences,
    private val navMan: NavMan,
    private val json: Json,
    private val scpSignaler: ScpSignaler,
    private val queuey: Queuey
) : ScpsViewModel(), ScpSignaler.Listener {
    // properties
    var adapter: ScpsAdapter? = null

    val sortField = MutableLiveData(preferences.defaultScpSortField.value)
    val sortOrder = MutableLiveData(preferences.defaultScpSortOrder.value)
    val query = MutableLiveData<String?>(null)
    val randomNumber = MutableLiveData<Int?>(null)
    val series = MutableLiveData<Int?>(null)

    val canRefresh = MutableLiveData(true)
    val isRefreshing = MutableLiveData(false)

    // init
    init {
        scpSignaler.add(this)
        onRefreshAction()
    }

    override fun onCleared() {
        super.onCleared()
        scpSignaler.remove(this)
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
                val scps = scpsService.getScps(dto)

                if (refresh) {
                    items.value?.clear()
                    items.value?.addAll(scps)
                    adapter?.notifyDataSetChanged()
                } else if (scps.isNotEmpty()) {
                    items.value?.addAll(scps)
                    adapter?.notifyItemInserted(items.value!!.size)
                }

                if (randomNumber.value != null) {
                    randomNumber.value = null
                }
                state.value = when (scps.size < (dto.limit ?: ScpsConstants.SCPS_PAGE_SIZE)) {
                    true -> PageState.Bottom
                    else -> PageState.Idle
                }
                failedToLoad.value = false
                isRefreshing.value = false
            } catch (e: Throwable) {
                Timber.e(e)
                if (randomNumber.value != null) {
                    randomNumber.value = null
                }
                failedToLoad.value = true
                state.value = PageState.Bottom
                toastMessage.value = e.message
                isRefreshing.value = false
            }
        }
    }

    private fun provideFilterDto(refresh: Boolean): GetScpsFilterDto {
        var cursor: String? = null
        items.value?.lastOrNull()?.let { lastScp ->
            try {
                val map = json.encodeToJsonElement(lastScp).jsonObject.toMap()
                map[sortField.value?.rawValue]?.let { fieldValue ->
                    cursor = fieldValue.toString().replace("\"", "")
                }
            } catch (e: Throwable) {
                Timber.e(e)
            }
        }

        val sort = "${sortField.value?.rawValue},${sortOrder.value?.rawValue}"

        return GetScpsFilterDto(
            null,
            null,
            query.value,
            randomNumber.value,
            series.value,
            null,
            ScpVisibility.VISIBLE,
            ScpStatus.APPROVED,
            sort,
            if (refresh) null else cursor,
            if (refresh) ScpsConstants.SCPS_PAGE_SIZE_REFRESH else ScpsConstants.SCPS_PAGE_SIZE
        )
    }

    fun handleOnSubmitQuery(query: String?) {
        this.query.value = query
        didChangeSort()
    }

    fun handleOnTapMenuSort(view: View?) {
        val options = mutableListOf<Pair<String, () -> Unit>>()

        val seriesName = if (series.value == null) "Series (All)" else "Series (${series.value})"
        options.add(Pair(seriesName) {
            handleOnTapMenuSortSeries(view)
        })

        options.add(Pair("") {})

        ScpSortField.allCases().forEach {
            val name = if (it == sortField.value) "${it.displayName()}*" else it.displayName()
            options.add(Pair(name) {
                handleOnTapMenuSortField(view, it)
            })
        }

        options.add(Pair("") {})

        options.add(Pair("Random") {
            if (state.value != PageState.Fetching) {
                val series = series.value
                val random: Int
                if (series != null) {
                    val seriesUpperBounds = series * 1000 - 1
                    var seriesLowerBounds = seriesUpperBounds - 999
                    if (seriesLowerBounds < ScpsConstants.SCPS_LOWEST_NUMBER) {
                        seriesLowerBounds = ScpsConstants.SCPS_LOWEST_NUMBER
                    }
                    random = (seriesLowerBounds..seriesUpperBounds).random()
                } else {
                    random = (ScpsConstants.SCPS_LOWEST_NUMBER..ScpsConstants.SCPS_COUNT).random()
                }

                randomNumber.value = random
                didChangeSort()
            }
        })

        view?.makePopupMenu(options.map { it.first }) {
            options[it].second.invoke()
        }
    }

    private fun handleOnTapMenuSortSeries(view: View?) {
        val options = mutableListOf<Pair<String, () -> Unit>>()

        for (i in 0 until 8) {
            val name = if (i == 0) "All Series" else { if (series.value == i) "Series ${i}*" else "Series $i" }
            options.add(Pair(name) {
                series.value = if (i == 0) null else i
                didChangeSort()
            })
        }

        view?.makePopupMenu(options.map { it.first }) {
            options[it].second.invoke()
        }
    }

    private fun handleOnTapMenuSortField(view: View?, field: ScpSortField) {
        val options = mutableListOf<Pair<String, () -> Unit>>()

        ScpSortOrder.allCases().forEach {
            val name = if (sortField.value == field && it == sortOrder.value) "${it.displayName()}*" else it.displayName()
            options.add(Pair(name) {
                sortField.value = field
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
                        adapter?.notifyItemChanged(index)
                    }
                }
            }
        }
    }
}
