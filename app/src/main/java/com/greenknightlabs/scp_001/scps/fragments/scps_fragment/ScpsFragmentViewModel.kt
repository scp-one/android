package com.greenknightlabs.scp_001.scps.fragments.scps_fragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.greenknightlabs.scp_001.app.enums.PageState
import com.greenknightlabs.scp_001.app.fragments.PageViewModel
import com.greenknightlabs.scp_001.app.util.NavMan
import com.greenknightlabs.scp_001.app.util.Preferences
import com.greenknightlabs.scp_001.auth.util.AuthMan
import com.greenknightlabs.scp_001.posts.config.PostsConstants
import com.greenknightlabs.scp_001.posts.dtos.GetPostsFilterDto
import com.greenknightlabs.scp_001.posts.enums.PostVisibility
import com.greenknightlabs.scp_001.scps.ScpsService
import com.greenknightlabs.scp_001.scps.config.ScpsConstants
import com.greenknightlabs.scp_001.scps.dtos.GetScpsFilterDto
import com.greenknightlabs.scp_001.scps.enums.ScpSortField
import com.greenknightlabs.scp_001.scps.enums.ScpStatus
import com.greenknightlabs.scp_001.scps.enums.ScpVisibility
import com.greenknightlabs.scp_001.scps.fragments.scps_fragment.adapters.ScpsFragmentAdapter
import com.greenknightlabs.scp_001.scps.models.Scp
import com.greenknightlabs.scp_001.scps.view_holders.ScpComponentViewHolder
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
    private val authMan: AuthMan,
    private val preferences: Preferences,
    private val navMan: NavMan,
    private val json: Json
) : PageViewModel<Scp>(), ScpComponentViewHolder.Listener {
    // properties
    var adapter: ScpsFragmentAdapter? = null

    val sortField = preferences.defaultScpSortField
    val sortOrder = preferences.defaultScpSortOrder
    val series = MutableLiveData<Int?>(null)

    val canRefresh = MutableLiveData(true)
    val isRefreshing = MutableLiveData(false)

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
                val scps = scpsService.getScps(dto)

                if (refresh) {
                    items.value?.clear()
                    items.value?.addAll(scps)
                    adapter?.notifyDataSetChanged()
                } else if (scps.isNotEmpty()) {
                    items.value?.addAll(scps)
                    adapter?.notifyItemInserted(items.value!!.size)
                }

                state.value = when (scps.size < (dto.limit ?: ScpsConstants.SCPS_PAGE_SIZE)) {
                    true -> PageState.Bottom
                    else -> PageState.Idle
                }
                failedToLoad.value = false
                isRefreshing.value = false
            } catch (e: Throwable) {
                Timber.e(e)
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
            null,
            null,
            series.value,
            null,
            ScpVisibility.VISIBLE,
            ScpStatus.APPROVED,
            sort,
            if (refresh) null else cursor,
            if (refresh) ScpsConstants.SCPS_PAGE_SIZE_REFRESH else ScpsConstants.SCPS_PAGE_SIZE
        )
    }

    override fun handleOnTapScp(scp: Scp) {

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