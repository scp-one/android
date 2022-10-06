package com.greenknightlabs.scp_001.posts.fragments.posts_fragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.greenknightlabs.scp_001.app.enums.PageState
import com.greenknightlabs.scp_001.app.fragments.PageViewModel
import com.greenknightlabs.scp_001.app.util.NavMan
import com.greenknightlabs.scp_001.app.util.Preferences
import com.greenknightlabs.scp_001.auth.util.AuthMan
import com.greenknightlabs.scp_001.posts.PostsService
import com.greenknightlabs.scp_001.posts.config.PostsConstants
import com.greenknightlabs.scp_001.posts.dtos.GetPostsFilterDto
import com.greenknightlabs.scp_001.posts.enums.PostSortField
import com.greenknightlabs.scp_001.posts.enums.PostSortOrder
import com.greenknightlabs.scp_001.posts.enums.PostStatus
import com.greenknightlabs.scp_001.posts.enums.PostVisibility
import com.greenknightlabs.scp_001.posts.models.Post
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
class PostsFragmentViewModel @Inject constructor(
    private val postsService: PostsService,
    private val authMan: AuthMan,
    private val preferences: Preferences,
    private val navMan: NavMan,
    private val json: Json
) : PageViewModel<Post>() {
    // properties
    val sortField = MutableLiveData(PostSortField.PUBLISHED_AT)
    val sortOrder = MutableLiveData(PostSortOrder.DESCENDING)
    val postStatus = MutableLiveData(PostStatus.APPROVED)

    val didRefresh = MutableLiveData(false)
    val didInsertBefore = MutableLiveData(false)
    val didInsertAfter = MutableLiveData(false)
    val didDelete = MutableLiveData(false)

    val canRefresh = MutableLiveData(true)
    val isRefreshing = MutableLiveData(false)

    // init
    init {
        onRefreshAction()
    }

    // functions
    fun onRefreshAction() {
        Timber.d("On refresh action")
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
                val posts = postsService.getPosts(dto)

                if (refresh) {
                    items.value?.clear()
                    items.value?.addAll(posts)
                    didRefresh.value = true
                } else if (posts.isNotEmpty()) {
                    items.value?.addAll(posts)
                    didInsertAfter.value = true
                }

                state.value = when (posts.size < (dto.limit ?: PostsConstants.POSTS_PAGE_SIZE)) {
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

    private fun provideFilterDto(refresh: Boolean): GetPostsFilterDto {
        var cursor: String? = null
        items.value?.lastOrNull()?.let { lastPost ->
            try {
                val map = json.encodeToJsonElement(lastPost).jsonObject.toMap()
                map[sortField.value?.rawValue]?.let { fieldValue ->
                    cursor = fieldValue.toString().replace("\"", "")
                }
            } catch (e: Throwable) {
                Timber.e(e)
            }
        }

        val sort = "${sortField.value?.rawValue},${sortOrder.value?.rawValue}"

        return GetPostsFilterDto(
            null,
            null,
            PostVisibility.VISIBLE,
            postStatus.value,
            sort,
            if (refresh) null else cursor,
            if (refresh) PostsConstants.POSTS_PAGE_SIZE_REFRESH else PostsConstants.POSTS_PAGE_SIZE
        )
    }
}