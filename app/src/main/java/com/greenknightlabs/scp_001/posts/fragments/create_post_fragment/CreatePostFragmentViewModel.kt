package com.greenknightlabs.scp_001.posts.fragments.create_post_fragment

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
import com.greenknightlabs.scp_001.posts.enums.PostStatus
import com.greenknightlabs.scp_001.posts.fragments.create_post_fragment.interfaces.AttachMediaComponentListener
import com.greenknightlabs.scp_001.posts.fragments.local_posts_fragment.LocalPostsFragment
import com.greenknightlabs.scp_001.posts.fragments.local_posts_fragment.local_posts.getPostPostingGuidelines
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreatePostFragmentViewModel @Inject constructor(
    private val postsService: PostsService,
    private val navMan: NavMan
) : BaseViewModel(), AttachMediaComponentListener, MediaCollectionFragment.Listener {
    // properties
    val title = MutableLiveData("")
    val content = MutableLiveData("")
    val media = MutableLiveData<Media?>(null)

    val isLocked = MutableLiveData(false)

    // functions
    fun handleOnTapMenuSend(view: View?) {
        val options = listOf("Save as draft", "Upload")

        view?.makePopupMenu(options) {
            when (it) {
                0 -> save()
                1 -> upload()
            }
        }
    }

    private fun save() {
        val title = title.value ?: return
        val content = content.value ?: return

        if (title.isEmpty() || content.isEmpty()) {
            toastMessage.value = "Missing title or content"
            return
        }

        isLocked.value = true
        state.value = PageState.Fetching

        viewModelScope.launch {
            val dto = CreatePostDto(title, content, media.value?.id, PostStatus.DRAFT, null)

            try {
                val post = postsService.createPost(dto)

                isLocked.value = false
                state.value = PageState.Idle

                navMan.popFragment()
            } catch (e: Throwable) {
                isLocked.value = false
                state.value = PageState.Idle
                toastMessage.value = e.message
            }
        }
    }

    private fun upload() {
        val title = title.value ?: return
        val content = content.value ?: return

        if (title.isEmpty() || content.isEmpty()) {
            toastMessage.value = "Missing title or content"
            return
        }

        isLocked.value = true
        state.value = PageState.Fetching

        viewModelScope.launch {
            val dto = CreatePostDto(title, content, media.value?.id, PostStatus.PENDING, null)

            try {
                val post = postsService.createPost(dto)

                isLocked.value = false
                state.value = PageState.Idle

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
