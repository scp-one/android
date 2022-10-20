package com.greenknightlabs.scp_001.posts.fragments.post_fragment

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.greenknightlabs.scp_001.actions.PostActionsService
import com.greenknightlabs.scp_001.actions.dtos.CreatePostActionsDto
import com.greenknightlabs.scp_001.actions.enums.PostActionsType
import com.greenknightlabs.scp_001.app.extensions.makePopupMenu
import com.greenknightlabs.scp_001.app.fragments.base_fragment.BaseViewModel
import com.greenknightlabs.scp_001.app.util.NavMan
import com.greenknightlabs.scp_001.app.util.Queuey
import com.greenknightlabs.scp_001.auth.util.AuthMan
import com.greenknightlabs.scp_001.posts.PostsService
import com.greenknightlabs.scp_001.posts.fragments.edit_post_fragment.EditPostFragment
import com.greenknightlabs.scp_001.posts.interfaces.PostAuthorComponentListener
import com.greenknightlabs.scp_001.posts.models.Post
import com.greenknightlabs.scp_001.posts.util.PostSignaler
import com.greenknightlabs.scp_001.reports.PostReportsService
import com.greenknightlabs.scp_001.users.fragments.user_profile_fragment.UserProfileFragment
import com.greenknightlabs.scp_001.users.models.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PostFragmentViewModel @Inject constructor(
    private val postsService: PostsService,
    private val postActionsService: PostActionsService,
    private val postReportsService: PostReportsService,
    private val authMan: AuthMan,
    private val navMan: NavMan,
    private val postSignaler: PostSignaler,
    private val queuey: Queuey
) : BaseViewModel(), PostAuthorComponentListener, PostSignaler.Listener {
    // properties
    val post = MutableLiveData<Post?>(null)
    val wasDeleted = MutableLiveData(false)

    val confirmAlertText = MutableLiveData("")
    val confirmAlertAction: MutableLiveData<() -> Unit> = MutableLiveData()
    val shouldShowConfirmAlert = MutableLiveData(false)

    // init
    init {
        postSignaler.add(this)
    }

    override fun onCleared() {
        postSignaler.remove(this)
        super.onCleared()
    }

    // functions
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
                }
            }
            is PostSignaler.PostSignal.PostDidDelete -> {
                if (post.value?.id == signal.post.id) {
                    wasDeleted.value = true
                }
            }
        }
    }
}
