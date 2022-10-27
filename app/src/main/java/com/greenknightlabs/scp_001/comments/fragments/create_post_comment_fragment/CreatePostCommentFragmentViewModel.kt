package com.greenknightlabs.scp_001.comments.fragments.create_post_comment_fragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.greenknightlabs.scp_001.app.config.AppConstants
import com.greenknightlabs.scp_001.app.enums.PageState
import com.greenknightlabs.scp_001.app.fragments.base_fragment.BaseViewModel
import com.greenknightlabs.scp_001.app.util.NavMan
import com.greenknightlabs.scp_001.comments.PostCommentsService
import com.greenknightlabs.scp_001.comments.config.CommentsConstants
import com.greenknightlabs.scp_001.comments.dtos.CreatePostCommentDto
import com.greenknightlabs.scp_001.comments.util.PostCommentsSignaler
import com.greenknightlabs.scp_001.posts.models.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okio.utf8Size
import javax.inject.Inject

@HiltViewModel
class CreatePostCommentFragmentViewModel @Inject constructor(
    private val postCommentsService: PostCommentsService,
    private val postCommentsSignaler: PostCommentsSignaler,
    private val navMan: NavMan
) : BaseViewModel() {
    // properties
    var post: Post? = null
    val content = MutableLiveData("")
    val charactersLeft = MutableLiveData(calculateCharactersLeft().toString())

    val isLocked = MutableLiveData(false)

    // functions
    fun calculateCharactersLeft(): Int {
        val content = content.value ?: return 0
        return CommentsConstants.POST_COMMENT_MAX_LENGTH - (content.codePointCount(0, content.length))
    }

    fun handleOnTapUpload() {
        upload()
    }

    private fun upload() {
        val post = post ?: return
        val content = content.value ?: return

        if (content.isEmpty()) {
            toastMessage.value = "Missing content"
            return
        }

        if (calculateCharactersLeft() < 0) {
            toastMessage.value = "Your comment is over the character limit"
            return
        }

        isLocked.value = true
        state.value = PageState.Fetching

        viewModelScope.launch {
            val dto = CreatePostCommentDto(content)

            try {
                val postComment = postCommentsService.createPostComment(post.id, dto)

                isLocked.value = false
                state.value = PageState.Idle

                postCommentsSignaler.send(PostCommentsSignaler.PostCommentSignal.PostCommentDidCreate(postComment))
                navMan.popFragment()
            } catch (e: Throwable) {
                isLocked.value = false
                state.value = PageState.Idle
                toastMessage.value = e.message
            }
        }
    }
}
