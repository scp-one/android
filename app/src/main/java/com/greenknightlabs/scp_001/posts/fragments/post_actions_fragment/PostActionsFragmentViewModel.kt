package com.greenknightlabs.scp_001.posts.fragments.post_actions_fragment

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.greenknightlabs.scp_001.actions.PostActionsService
import com.greenknightlabs.scp_001.actions.config.ActionsConstants
import com.greenknightlabs.scp_001.actions.dtos.CreatePostActionsDto
import com.greenknightlabs.scp_001.actions.dtos.GetPostActionsFilterDto
import com.greenknightlabs.scp_001.actions.enums.PostActionsSortField
import com.greenknightlabs.scp_001.actions.enums.PostActionsType
import com.greenknightlabs.scp_001.actions.models.PostActions
import com.greenknightlabs.scp_001.app.enums.PageState
import com.greenknightlabs.scp_001.app.extensions.makePopupMenu
import com.greenknightlabs.scp_001.app.fragments.base_fragment.BaseViewModel
import com.greenknightlabs.scp_001.app.util.NavMan
import com.greenknightlabs.scp_001.auth.util.AuthMan
import com.greenknightlabs.scp_001.posts.PostsService
import com.greenknightlabs.scp_001.posts.PostsViewModel
import com.greenknightlabs.scp_001.posts.adapters.PostsAdapter
import com.greenknightlabs.scp_001.posts.enums.PostSortOrder
import com.greenknightlabs.scp_001.posts.fragments.edit_post_fragment.EditPostFragment
import com.greenknightlabs.scp_001.posts.fragments.post_fragment.PostFragment
import com.greenknightlabs.scp_001.posts.models.Post
import com.greenknightlabs.scp_001.posts.util.PostSignaler
import com.greenknightlabs.scp_001.reports.PostReportsService
import com.greenknightlabs.scp_001.users.fragments.user_profile_fragment.UserProfileFragment
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
class PostActionsFragmentViewModel @Inject constructor(
    private val postActionsService: PostActionsService,
    private val postReportsService: PostReportsService,
    private val postsService: PostsService,
    private val authMan: AuthMan,
    private val navMan: NavMan,
    private val json: Json,
    private val postSignaler: PostSignaler
) : PostsViewModel(), PostSignaler.Listener {
    // properties
    var adapter: PostsAdapter? = null

    val actionType = MutableLiveData(PostActionsType.LIKED)
    val sortOrder = MutableLiveData(PostSortOrder.DESCENDING)

    val canRefresh = MutableLiveData(true)
    val isRefreshing = MutableLiveData(false)
    private val actionsList = MutableLiveData<MutableList<PostActions>>(mutableListOf())

    val confirmAlertText = MutableLiveData("")
    val confirmAlertAction: MutableLiveData<() -> Unit> = MutableLiveData()
    val shouldShowConfirmAlert = MutableLiveData(false)

    // init
    init {
        postSignaler.add(this)
        onRefreshAction()
    }

    override fun onCleared() {
        super.onCleared()
        postSignaler.remove(this)
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
                val actions = postActionsService.getPostActions(dto)
                val posts = actions.filter { it.post.isLoaded }.map { it.post.post!! }

                if (refresh) {
                    actionsList.value?.clear()
                    items.value?.clear()
                    items.value?.addAll(posts)
                    adapter?.notifyDataSetChanged()
                } else if (actions.isNotEmpty()) {
                    items.value?.addAll(posts)
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

    private fun provideFilterDto(refresh: Boolean): GetPostActionsFilterDto {
        val sortField = when (actionType.value!!) {
            PostActionsType.LIKED -> PostActionsSortField.LIKED_AT
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

        return GetPostActionsFilterDto(
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

        PostActionsType.allCases().forEach {
            val name = if (it == actionType.value) "${it.displayName()}*" else it.displayName()
            options.add(Pair(name) {
                actionType.value = it
                didChangeSort()
            })
        }

        options.add(Pair("") {})

        PostSortOrder.allCases().forEach {
            val name = if (it == sortOrder.value) "${it.displayName()}*" else it.displayName()
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

    override fun handleOnTapPost(post: Post) {
        val postFragment = PostFragment()
        postFragment.post = post
        navMan.pushFragment(postFragment)
    }

    override fun handleOnTapPostAuthor(post: Post) {
        pushUserProfileFragment(post.user.user)
    }

    private fun pushUserProfileFragment(user: User?) {
        if (user == null) return

        val userProfileFragment = UserProfileFragment()
        userProfileFragment.user = user
        navMan.pushFragment(userProfileFragment)
    }

    override fun handleOnTapPostAuthorMore(post: Post, view: View) {
        val options = mutableListOf<Pair<String, () -> Unit>>()

        options.add(Pair("Visit User Profile") {
            pushUserProfileFragment(post.user.user)
        })
        options.add(Pair(if (!post.liked) "Like Post" else "Unlike Post") {
            didChangePostActions(PostActionsType.LIKED, post)
        })
        options.add(Pair("Report Post") {
            reportPost(post)
        })

        if (authMan.payload?.id == post.user.id) {
            options.add(Pair("") {})

            options.add(Pair("Edit Post") {
                pushEditPostFragment(post)
            })
            options.add(Pair("Delete Post") {
                handleOnTapDeletePost(post)
            })
        }

        view.makePopupMenu(options.map { it.first }) {
            options[it].second.invoke()
        }
    }

    private fun didChangePostActions(type: PostActionsType, post: Post) {
        val state: Boolean

        when (type) {
            PostActionsType.LIKED -> {
                post.liked = !post.liked
                state = post.liked
            }
        }

        postSignaler.send(PostSignaler.PostSignal.PostDidChange(post))

        viewModelScope.launch {
            val dto = CreatePostActionsDto(type, state)

            try {
                postActionsService.createPostAction(post.id, dto)
            } catch (e: Throwable) {
                toastMessage.value = e.message
            }
        }
    }

    private fun pushEditPostFragment(post: Post) {
        val editPostFragment = EditPostFragment()
        editPostFragment.post = post
        navMan.pushFragment(editPostFragment, true)
    }

    private fun handleOnTapDeletePost(post: Post) {
        confirmAlertText.value = "Are you sure you want to delete this post?"
        confirmAlertAction.value = {
            postSignaler.send(PostSignaler.PostSignal.PostDidDelete(post))

            viewModelScope.launch {
                try {
                    postsService.deletePost(post.id)
                } catch (e: Throwable) {
                    toastMessage.value = e.message
                }
            }
        }
        shouldShowConfirmAlert.value = true
    }

    private fun reportPost(post: Post) {
        viewModelScope.launch {
            try {
                postReportsService.createPostReport(post.id)
            } catch (e: Throwable) {
                toastMessage.value = e.message
            }
        }
    }

    // PostSignaler.Listener
    override fun handleSignal(signal: PostSignaler.PostSignal) {
        when (signal) {
            is PostSignaler.PostSignal.PostDidChange -> {
                items.value?.forEachIndexed { index, post ->
                    if (post.id == signal.post.id) {
                        items.value?.set(index, signal.post)
                        adapter?.notifyItemChanged(index)
                    }
                }
            }
            is PostSignaler.PostSignal.PostDidDelete -> {
                items.value?.reversed()?.forEachIndexed { index, post ->
                    if (post.id == signal.post.id) {
                        items.value?.removeAt(index)
                        adapter?.notifyItemRemoved(index)
                    }
                }
            }
        }
    }
}
