package com.greenknightlabs.scp_001.posts.fragments.posts_fragment

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.ConcatAdapter
import com.greenknightlabs.scp_001.actions.PostActionsService
import com.greenknightlabs.scp_001.actions.dtos.CreatePostActionsDto
import com.greenknightlabs.scp_001.actions.enums.PostActionsType
import com.greenknightlabs.scp_001.app.adapters.PageAdapter
import com.greenknightlabs.scp_001.app.enums.PageState
import com.greenknightlabs.scp_001.app.extensions.makePopupMenu
import com.greenknightlabs.scp_001.app.fragments.pro_access_fragment.ProAccessFragment
import com.greenknightlabs.scp_001.app.util.NavMan
import com.greenknightlabs.scp_001.app.util.Preferences
import com.greenknightlabs.scp_001.app.util.Queuey
import com.greenknightlabs.scp_001.app.util.shopkeep.Shopkeep
import com.greenknightlabs.scp_001.auth.util.AuthMan
import com.greenknightlabs.scp_001.posts.PostsService
import com.greenknightlabs.scp_001.posts.PostsViewModel
import com.greenknightlabs.scp_001.posts.config.PostsConstants
import com.greenknightlabs.scp_001.posts.dtos.GetPostsFilterDto
import com.greenknightlabs.scp_001.posts.enums.PostSortField
import com.greenknightlabs.scp_001.posts.enums.PostSortOrder
import com.greenknightlabs.scp_001.posts.enums.PostStatus
import com.greenknightlabs.scp_001.posts.enums.PostVisibility
import com.greenknightlabs.scp_001.posts.fragments.create_post_fragment.CreatePostFragment
import com.greenknightlabs.scp_001.posts.fragments.edit_post_fragment.EditPostFragment
import com.greenknightlabs.scp_001.posts.fragments.post_fragment.PostFragment
import com.greenknightlabs.scp_001.posts.adapters.PostsAdapter
import com.greenknightlabs.scp_001.posts.fragments.posts_fragment.adapters.UserCollectionComponentAdapter
import com.greenknightlabs.scp_001.posts.fragments.posts_fragment.adapters.UserCollectionComponentUserAdapter
import com.greenknightlabs.scp_001.posts.fragments.posts_fragment.view_holders.UserCollectionComponentUserViewHolder
import com.greenknightlabs.scp_001.posts.models.Post
import com.greenknightlabs.scp_001.posts.util.PostSignaler
import com.greenknightlabs.scp_001.reports.PostReportsService
import com.greenknightlabs.scp_001.users.UsersService
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
class PostsFragmentViewModel @Inject constructor(
    private val postsService: PostsService,
    private val postActionsService: PostActionsService,
    private val postReportsService: PostReportsService,
    private val usersService: UsersService,
    private val authMan: AuthMan,
    private val navMan: NavMan,
    private val json: Json,
    private val postSignaler: PostSignaler,
    private val queuey: Queuey,
    private val shopkeep: Shopkeep
) : PostsViewModel(), PostSignaler.Listener, UserCollectionComponentUserViewHolder.Listener {
    // properties
    var headerAdapter: UserCollectionComponentAdapter? = null
    var headerSubAdapter: UserCollectionComponentUserAdapter? = null
    var itemsAdapter: PostsAdapter? = null
    var pageAdapter: PageAdapter<Post>? = null
    var adapter: ConcatAdapter? = null

    val users = MutableLiveData<MutableList<User>>(mutableListOf())
    val isLoadingHeaderAdapter = MutableLiveData(false)
    val hasLoadedHeaderAdapter = MutableLiveData(false)

    private val sortField = MutableLiveData(PostSortField.PUBLISHED_AT)
    private val sortOrder = MutableLiveData(PostSortOrder.DESCENDING)
    private val postStatus = MutableLiveData(PostStatus.APPROVED)

    private val canRefresh = MutableLiveData(true)
    val isRefreshing = MutableLiveData(false)

    val confirmAlertText = MutableLiveData("")
    val confirmAlertAction: MutableLiveData<() -> Unit> = MutableLiveData()
    val shouldShowConfirmAlert = MutableLiveData(false)

    // init
    init {
        postSignaler.add(this)
        onRefreshAction()
        if (shopkeep.hasProAccess()) {
            loadUsers()
        }
    }

    override fun onCleared() {
        postSignaler.remove(this)
        super.onCleared()
    }

    // functions
    fun loadUsers() {
        isLoadingHeaderAdapter.value = true

        viewModelScope.launch {
            try {
                val user = usersService.getUserFromRequest()
                val originalItemCount = users.value?.size ?: 0
                users.value?.clear()
                headerSubAdapter?.notifyItemRangeRemoved(0, originalItemCount)

                users.value?.add(user)
                headerSubAdapter?.notifyItemRangeInserted(0, users.value?.size ?: 0)

                hasLoadedHeaderAdapter.value = true
                isLoadingHeaderAdapter.value = false
            } catch (e: Throwable) {
                toastMessage.value = e.message
            }
        }
    }

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
                val posts = postsService.getPosts(dto)

                if (refresh) {
                    val originalItemCount = items.value?.size ?: 0
                    items.value?.clear()
                    itemsAdapter?.notifyItemRangeRemoved(0, originalItemCount)

                    items.value?.addAll(posts)
                    itemsAdapter?.notifyItemRangeInserted(0, items.value?.size ?: 0)
                } else if (posts.isNotEmpty()) {
                    val rangeStart = (items.value?.size ?: 0)
                    items.value?.addAll(posts)
                    itemsAdapter?.notifyItemRangeInserted(rangeStart, posts.size)
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

    fun handleOnTapMenuSort(view: View?) {
        val options = mutableListOf<Pair<String, () -> Unit>>()

        PostSortField.allCases().forEach {
            val name = if (it == sortField.value) "${it.displayName()}*" else it.displayName()
            options.add(Pair(name) {
                handleOnTapMenuSortField(view, it)
            })
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

    fun handleOnTapMenuPost() {
        if (shopkeep.hasProAccess()) {
            navMan.pushFragment(CreatePostFragment(), true)
        } else {
            navMan.pushFragment(ProAccessFragment(), true)
        }
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

        queuey.queue({
            viewModelScope.launch {
                val dto = CreatePostActionsDto(type, state)

                try {
                    postActionsService.createPostAction(post.id, dto)
                } catch (e: Throwable) {
                    toastMessage.value = e.message
                }
            }
        }, "${post.id}${type.rawValue}")
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
                        itemsAdapter?.notifyItemChanged(index)
                    }
                }
            }
            is PostSignaler.PostSignal.PostDidDelete -> {
                val list = items.value ?: return

                for (index in list.size - 1 downTo 0) {
                    if (list[index].id == signal.post.id) {
                        items.value?.removeAt(index)
                        itemsAdapter?.notifyItemRemoved(index)
                    }
                }
            }
        }
    }

    // UserCollectionComponentUserViewHolder.Listener
    override fun handleOnTapUser(user: User) {
        val userProfileFragment = UserProfileFragment()
        userProfileFragment.user = user
        navMan.pushFragment(userProfileFragment)
    }
}
