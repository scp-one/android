package com.greenknightlabs.scp_001.posts.fragments.edit_post_fragment

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.greenknightlabs.scp_001.app.enums.PageState
import com.greenknightlabs.scp_001.app.extensions.makePopupMenu
import com.greenknightlabs.scp_001.app.fragments.base_fragment.BaseViewModel
import com.greenknightlabs.scp_001.app.util.NavMan
import com.greenknightlabs.scp_001.media.fragments.media_collection_fragment.MediaCollectionFragment
import com.greenknightlabs.scp_001.media.models.Media
import com.greenknightlabs.scp_001.posts.PostsService
import com.greenknightlabs.scp_001.posts.dtos.CreatePostDto
import com.greenknightlabs.scp_001.posts.dtos.EditPostDto
import com.greenknightlabs.scp_001.posts.enums.PostStatus
import com.greenknightlabs.scp_001.posts.fragments.create_post_fragment.interfaces.AttachMediaComponentListener
import com.greenknightlabs.scp_001.posts.fragments.local_posts_fragment.LocalPostsFragment
import com.greenknightlabs.scp_001.posts.fragments.local_posts_fragment.local_posts.getPostPostingGuidelines
import com.greenknightlabs.scp_001.posts.models.Post
import com.greenknightlabs.scp_001.posts.util.PostSignaler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditPostFragmentViewModel @Inject constructor(
    private val postsService: PostsService,
    private val navMan: NavMan,
    private val postSignaler: PostSignaler
) : BaseViewModel(), AttachMediaComponentListener, MediaCollectionFragment.Listener {
    // properties
    val post = MutableLiveData<Post?>(null)

    val title = MutableLiveData("")
    val content = MutableLiveData("")
    val media = MutableLiveData<Media?>(null)

    val isLocked = MutableLiveData(false)

    // functions
    fun handleOnTapMenuEdit(view: View?) {
        val options = listOf("Save as draft", "Upload")

        view?.makePopupMenu(options) {
            when (it) {
                0 -> edit(true)
                1 -> edit(false)
            }
        }
    }

    private fun edit(asDraft: Boolean) {
        val postId = post.value?.id ?: return
        val title = title.value ?: return
        val content = content.value ?: return

        if (title.isEmpty() || content.isEmpty()) {
            toastMessage.value = "Missing title or content"
            return
        }

        isLocked.value = true
        state.value = PageState.Fetching

        viewModelScope.launch {
            val nullify = mutableListOf<String>()
            var mediaId: String? = null

            if (media.value != null) {
                // empty media id means the media was recreated from PostMedia
                // this checks if the post media was changed
                if (media.value!!.id.isNotEmpty()) {
                    mediaId = media.value!!.id
                }
            } else {
                nullify.add("media")
            }

            val dto = EditPostDto(
                title,
                content,
                mediaId,
                null,
                if (asDraft) PostStatus.DRAFT else PostStatus.PENDING,
                null,
                nullify
            )

            try {
                val post = postsService.editPost(postId, dto)

                isLocked.value = false
                state.value = PageState.Idle

                postSignaler.send(PostSignaler.PostSignal.PostDidChange(post))

                navMan.popFragment()
            } catch (e: Throwable) {
                isLocked.value = false
                state.value = PageState.Idle
                toastMessage.value = e.message
            }
        }
    }

    fun handleOnTapPostingGuidelines() {
        val localPostsFragment = LocalPostsFragment()
        localPostsFragment.activityTitle = "Guidelines"
        localPostsFragment.posts = listOf(getPostPostingGuidelines())
        navMan.pushFragment(localPostsFragment, true)
    }

    override fun handleOnTapAttachMedia() {
        val mediaCollectionFragment = MediaCollectionFragment()
        mediaCollectionFragment.listener = this
        navMan.pushFragment(mediaCollectionFragment, true)
    }

    override fun handleOnTapRemoveMedia() {
        media.value = null
    }

    override fun handleMediaSelected(media: Media) {
        this.media.value = media
    }
}
