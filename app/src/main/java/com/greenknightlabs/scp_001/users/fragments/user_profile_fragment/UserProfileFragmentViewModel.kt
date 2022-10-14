package com.greenknightlabs.scp_001.users.fragments.user_profile_fragment

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.greenknightlabs.scp_001.app.enums.PageState
import com.greenknightlabs.scp_001.app.extensions.makePopupMenu
import com.greenknightlabs.scp_001.app.fragments.PageViewModel
import com.greenknightlabs.scp_001.app.util.NavMan
import com.greenknightlabs.scp_001.auth.util.AuthMan
import com.greenknightlabs.scp_001.posts.PostsService
import com.greenknightlabs.scp_001.posts.config.PostsConstants
import com.greenknightlabs.scp_001.posts.dtos.GetPostsFilterDto
import com.greenknightlabs.scp_001.posts.enums.PostSortField
import com.greenknightlabs.scp_001.posts.enums.PostSortOrder
import com.greenknightlabs.scp_001.posts.enums.PostStatus
import com.greenknightlabs.scp_001.posts.enums.PostVisibility
import com.greenknightlabs.scp_001.posts.fragments.post_fragment.PostFragment
import com.greenknightlabs.scp_001.posts.interfaces.PostAuthorComponentListener
import com.greenknightlabs.scp_001.posts.models.Post
import com.greenknightlabs.scp_001.posts.view_holders.PostComponentViewHolder
import com.greenknightlabs.scp_001.users.fragments.user_profile_fragment.adapters.UserProfileFragmentAdapter
import com.greenknightlabs.scp_001.users.models.User
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
class UserProfileFragmentViewModel @Inject constructor(
    private val postsService: PostsService,
    private val authMan: AuthMan,
    private val navMan: NavMan,
    private val json: Json
) : PageViewModel<Post>(), PostComponentViewHolder.Listener {
    // properties
    var adapter: UserProfileFragmentAdapter? = null
    var user: User? = null

    val uid = MutableLiveData<String?>(null)
    val sortField = MutableLiveData(PostSortField.PUBLISHED_AT)
    val sortOrder = MutableLiveData(PostSortOrder.DESCENDING)
    val postStatus = MutableLiveData(PostStatus.APPROVED)

    val canRefresh = MutableLiveData(true)
    val isRefreshing = MutableLiveData(false)

    // init
    init {
        user?.let { uid.value = it.id}
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
                val posts = postsService.getPosts(dto)

                if (refresh) {
                    items.value?.clear()
                    items.value?.addAll(posts)
                    adapter?.notifyDataSetChanged()
                } else if (posts.isNotEmpty()) {
                    items.value?.addAll(posts)
                    adapter?.notifyItemInserted(items.value!!.size)
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
            uid.value,
            PostVisibility.VISIBLE,
            postStatus.value,
            sort,
            if (refresh) null else cursor,
            if (refresh) PostsConstants.POSTS_PAGE_SIZE_REFRESH else PostsConstants.POSTS_PAGE_SIZE
        )
    }

    fun handleOnTapMenuSort(view: View?) {
        val options = mutableListOf<Pair<String, () -> Unit>>()

        PostSortField.allCases().forEach {
            val name = if (it == sortField.value) "${it.displayName()}*" else it.displayName()
            options.add(Pair(name) {
                handleOnTapMenuSortField(view, it)
            })
        }

        user?.let { user ->
            if (authMan.payload?.id == user.id) {
                options.add(Pair("") {})

                PostStatus.allCases().forEach {
                    val name = if(it == postStatus.value) "${it.displayName()}*" else it.displayName()
                    options.add(Pair(name) {
                        postStatus.value = it
                        didChangeSort()
                    })
                }
            }
        }

        view?.makePopupMenu(options.map { it.first }) {
            options[it].second.invoke()
        }
    }

    private fun handleOnTapMenuSortField(view: View?, field: PostSortField) {
        val options = mutableListOf<Pair<String, () -> Unit>>()

        PostSortOrder.allCases().forEach {
            val name = if (field == sortField.value && it == sortOrder.value) "${it.displayName(field)}*" else it.displayName(field)
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

    override fun handleOnTapPost(post: Post) {
        val postFragment = PostFragment()
        postFragment.post = post
        navMan.pushFragment(postFragment)
    }

    override fun handleOnTapPostAuthor(post: Post) {
        pushUserProfileFragment(post.user.user)
    }

    override fun handleOnTapPostAuthorMore(post: Post, view: View) {
        val options = listOf(
            "Visit User Profile",
            "Like Post",
            "Report Post"
        )

        view.makePopupMenu(options) { index ->
            when (index) {
                0 -> pushUserProfileFragment(post.user.user)
                1 -> Timber.d("liking post")
                2 -> Timber.d("reporting post")
            }
        }
    }

    private fun pushUserProfileFragment(user: User?) {
        if (user == null) return

        val userProfileFragment = UserProfileFragment()
        userProfileFragment.user = user
        navMan.pushFragment(userProfileFragment)
    }
}
