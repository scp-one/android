package com.greenknightlabs.scp_001.posts.fragments.post_fragment

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.ConcatAdapter
import com.greenknightlabs.scp_001.actions.PostActionsService
import com.greenknightlabs.scp_001.actions.dtos.CreatePostActionsDto
import com.greenknightlabs.scp_001.actions.enums.PostActionsType
import com.greenknightlabs.scp_001.app.enums.PageState
import com.greenknightlabs.scp_001.app.extensions.makePopupMenu
import com.greenknightlabs.scp_001.app.util.NavMan
import com.greenknightlabs.scp_001.app.util.Queuey
import com.greenknightlabs.scp_001.app.view_models.PageViewModel
import com.greenknightlabs.scp_001.auth.util.AuthMan
import com.greenknightlabs.scp_001.comments.PostCommentsService
import com.greenknightlabs.scp_001.comments.config.CommentsConstants
import com.greenknightlabs.scp_001.comments.dtos.GetPostCommentsFilterDto
import com.greenknightlabs.scp_001.comments.enums.PostCommentSortField
import com.greenknightlabs.scp_001.comments.enums.PostCommentSortOrder
import com.greenknightlabs.scp_001.comments.fragments.create_post_comment_fragment.CreatePostCommentFragment
import com.greenknightlabs.scp_001.comments.fragments.edit_post_comment_fragment.EditPostCommentFragment
import com.greenknightlabs.scp_001.comments.models.PostComment
import com.greenknightlabs.scp_001.comments.util.PostCommentsSignaler
import com.greenknightlabs.scp_001.posts.fragments.post_fragment.view_holders.PostCommentComponentViewHolder
import com.greenknightlabs.scp_001.posts.PostsService
import com.greenknightlabs.scp_001.posts.fragments.edit_post_fragment.EditPostFragment
import com.greenknightlabs.scp_001.posts.fragments.post_fragment.adapters.PostCommentPageAdapter
import com.greenknightlabs.scp_001.posts.fragments.post_fragment.adapters.PostCommentsAdapter
import com.greenknightlabs.scp_001.posts.fragments.post_fragment.adapters.PostDetailsAdapter
import com.greenknightlabs.scp_001.posts.fragments.post_fragment.view_holders.LoadCommentsComponentViewHolder
import com.greenknightlabs.scp_001.posts.fragments.post_fragment.view_holders.PostDetailsComponentViewHolder
import com.greenknightlabs.scp_001.posts.models.Post
import com.greenknightlabs.scp_001.posts.util.PostSignaler
import com.greenknightlabs.scp_001.reports.PostCommentReportsService
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
class PostFragmentViewModel @Inject constructor(
    private val postsService: PostsService,
    private val postCommentsService: PostCommentsService,
    private val postActionsService: PostActionsService,
    private val postReportsService: PostReportsService,
    private val postCommentReportsService: PostCommentReportsService,
    private val authMan: AuthMan,
    private val navMan: NavMan,
    private val postSignaler: PostSignaler,
    private val postCommentsSignaler: PostCommentsSignaler,
    private val queuey: Queuey,
    private val json: Json
) : PageViewModel<PostComment>(),
    PostSignaler.Listener,
    PostCommentsSignaler.Listener,
    PostDetailsComponentViewHolder.Listener,
    PostCommentComponentViewHolder.Listener,
    LoadCommentsComponentViewHolder.Listener
{
    // properties
    var headerAdapter: PostDetailsAdapter? = null
    var itemsAdapter: PostCommentsAdapter? = null
    var pageAdapter: PostCommentPageAdapter? = null
    var adapter: ConcatAdapter? = null

    val post = MutableLiveData<Post?>(null)
    val wasDeleted = MutableLiveData(false)

    private val sortField = MutableLiveData(PostCommentSortField.CREATED_AT)
    private val sortOrder = MutableLiveData(PostCommentSortOrder.DESCENDING)

    private val canRefresh = MutableLiveData(true)
    val isRefreshing = MutableLiveData(false)

    val tappedLoadComments = MutableLiveData(false)
    val hasNoComments = MutableLiveData(false)

    val webViewUrl = MutableLiveData<String?>(null)
    val shouldShowWebView = MutableLiveData(false)

    val confirmAlertText = MutableLiveData("")
    val confirmAlertAction: MutableLiveData<() -> Unit> = MutableLiveData()
    val shouldShowConfirmAlert = MutableLiveData(false)

    // init
    init {
        postSignaler.add(this)
        postCommentsSignaler.add(this)
    }

    override fun onCleared() {
        postSignaler.remove(this)
        postCommentsSignaler.remove(this)
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
                val postComments = postCommentsService.getPostComments(dto)

                if (refresh) {
                    val originalItemCount = items.value?.size ?: 0
                    items.value?.clear()
                    itemsAdapter?.notifyItemRangeRemoved(0, originalItemCount)

                    items.value?.addAll(postComments)
                    itemsAdapter?.notifyItemRangeInserted(0, items.value?.size ?: 0)
                } else if (postComments.isNotEmpty()) {
                    val rangeStart = (items.value?.size ?: 0)
                    items.value?.addAll(postComments)
                    itemsAdapter?.notifyItemRangeInserted(rangeStart, postComments.size)
                }

                state.value = when (postComments.size < (dto.limit ?: CommentsConstants.POST_COMMENTS_PAGE_SIZE)) {
                    true -> PageState.Bottom
                    else -> PageState.Idle
                }
                failedToLoad.value = false
                isRefreshing.value = false

                hasNoComments.value = (items.value?.size ?: 0) == 0 && state.value == PageState.Bottom
                pageAdapter?.notifyDataSetChanged()
            } catch (e: Throwable) {
                failedToLoad.value = true
                state.value = PageState.Bottom
                toastMessage.value = e.message
                isRefreshing.value = false
            }
        }
    }

    private fun provideFilterDto(refresh: Boolean): GetPostCommentsFilterDto {
        var cursor: String? = null
        items.value?.lastOrNull()?.let { lastComment ->
            try {
                val map = json.encodeToJsonElement(lastComment).jsonObject.toMap()
                map[sortField.value?.rawValue]?.let { fieldValue ->
                    cursor = fieldValue.toString().replace("\"", "")
                }
            } catch (e: Throwable) {
                Timber.e(e)
            }
        }

        val sort = "${sortField.value?.rawValue},${sortOrder.value?.rawValue}"

        return GetPostCommentsFilterDto(
            null,
            null,
            post.value?.id,
            sort,
            if (refresh) null else cursor,
            if (refresh) CommentsConstants.POST_COMMENTS_PAGE_SIZE_REFRESH else CommentsConstants.POST_COMMENTS_PAGE_SIZE
        )
    }

    override fun handleOnTapPostAuthor(post: Post) {
        pushUserProfileFragment(post.user.user)
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
            confirmAlertText.value = "Are you sure you want to report this post?"
            confirmAlertAction.value = { reportPost(post) }
            shouldShowConfirmAlert.value = true
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

    private fun pushUserProfileFragment(user: User?) {
        if (user == null) return

        val userProfileFragment = UserProfileFragment()
        userProfileFragment.user = user
        navMan.pushFragment(userProfileFragment)
    }

    private fun didChangePostActions(type: PostActionsType, post: Post) {
        if (wasDeleted.value == true) {
            toastMessage.value = "This post has been deleted"
            return
        }

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
        if (wasDeleted.value == true) {
            toastMessage.value = "This post has been deleted"
            return
        }

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

    override fun handleSignal(signal: PostSignaler.PostSignal) {
        when (signal) {
            is PostSignaler.PostSignal.PostDidChange -> {
                if (post.value?.id == signal.post.id) {
                    post.value = signal.post
                    headerAdapter?.notifyItemChanged(0)
                }
            }
            is PostSignaler.PostSignal.PostDidDelete -> {
                if (post.value?.id == signal.post.id) {
                    wasDeleted.value = true
                }
            }
        }
    }

    override fun handleSignal(signal: PostCommentsSignaler.PostCommentSignal) {
        when (signal) {
            is PostCommentsSignaler.PostCommentSignal.PostCommentDidCreate -> {
                if (signal.comment.post.id == post.value?.id) {
                    items.value?.add(0, signal.comment)
                    itemsAdapter?.notifyItemInserted(0)
                    hasNoComments.value = false
                    tappedLoadComments.value = true
                    pageAdapter?.notifyDataSetChanged()
                }
            }
            is PostCommentsSignaler.PostCommentSignal.PostCommentDidChange -> {
                items.value?.forEachIndexed { index, comment ->
                    if (comment.id == signal.comment.id) {
                        items.value?.set(index, signal.comment)
                        itemsAdapter?.notifyItemChanged(index)
                    }
                }
            }
            is PostCommentsSignaler.PostCommentSignal.PostCommentDidDelete -> {
                val list = items.value ?: return

                for (index in list.size - 1 downTo 0) {
                    if (list[index].id == signal.comment.id) {
                        items.value?.removeAt(index)
                        itemsAdapter?.notifyItemRemoved(index)
                    }
                }
            }
        }
    }

    override fun handleOnTapPostSource(url: String) {
        webViewUrl.value = url
        shouldShowWebView.value = true
    }

    override fun handleOnTapAddComment() {
        if (wasDeleted.value == true) {
            toastMessage.value = "This post has been deleted"
            return
        }

        val createPostCommentFragment = CreatePostCommentFragment()
        createPostCommentFragment.post = post.value
        navMan.pushFragment(createPostCommentFragment, true)
    }

    override fun handleOnTapMore(comment: PostComment, view: View) {
        val options = mutableListOf<Pair<String, () -> Unit>>()

        if (authMan.payload?.id == comment.user.id) {
            options.add(Pair("Edit Comment") {
                pushEditCommentFragment(comment)
            })
            options.add(Pair("Delete Comment") {
                confirmAlertText.value = "Are you sure you want to delete this comment?"
                confirmAlertAction.value = { deleteComment(comment) }
                shouldShowConfirmAlert.value = true
            })
            options.add(Pair("") {})
        }

        options.add(Pair("Report Comment") {
            confirmAlertText.value = "Are you sure you want to report this comment?"
            confirmAlertAction.value = { reportComment(comment) }
            shouldShowConfirmAlert.value = true
        })

        view.makePopupMenu(options.map { it.first }) {
            options[it].second.invoke()
        }
    }

    private fun pushEditCommentFragment(comment: PostComment) {
        if (wasDeleted.value == true) {
            toastMessage.value = "This post has been deleted"
            return
        }

        val editPostCommentFragment = EditPostCommentFragment()
        editPostCommentFragment.comment = comment
        navMan.pushFragment(editPostCommentFragment, true)
    }

    private fun deleteComment(comment: PostComment) {
        if (wasDeleted.value == true) {
            toastMessage.value = "This post has been deleted"
            return
        }

        if (state.value == PageState.Fetching) return

        val originalState = state.value
        state.value = PageState.Fetching

        viewModelScope.launch {
            try {
                postCommentsService.deletePostComment(comment.id)

                state.value = originalState

                postCommentsSignaler.send(PostCommentsSignaler.PostCommentSignal.PostCommentDidDelete(comment))
            } catch (e: Throwable) {
                state.value = originalState
                toastMessage.value = e.message
            }
        }
    }

    private fun reportComment(comment: PostComment) {
        if (wasDeleted.value == true) {
            toastMessage.value = "This post has been deleted"
            return
        }

        viewModelScope.launch {
            try {
                postCommentReportsService.createPostCommentReport(comment.id)
            } catch (e: Throwable) {
                toastMessage.value = e.message
            }
        }
    }

    override fun handleOnTapLoadComments() {
        if (wasDeleted.value == true) {
            toastMessage.value = "This post has been deleted"
            return
        }

        if (state.value == PageState.Fetching) return

        tappedLoadComments.value = true
        paginate(true)
    }
}
