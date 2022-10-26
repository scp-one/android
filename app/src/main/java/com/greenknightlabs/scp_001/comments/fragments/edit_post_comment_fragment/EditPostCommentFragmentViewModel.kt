package com.greenknightlabs.scp_001.comments.fragments.edit_post_comment_fragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.greenknightlabs.scp_001.app.enums.PageState
import com.greenknightlabs.scp_001.app.fragments.base_fragment.BaseViewModel
import com.greenknightlabs.scp_001.app.util.NavMan
import com.greenknightlabs.scp_001.comments.PostCommentsService
import com.greenknightlabs.scp_001.comments.config.CommentsConstants
import com.greenknightlabs.scp_001.comments.dtos.EditPostCommentDto
import com.greenknightlabs.scp_001.comments.models.PostComment
import com.greenknightlabs.scp_001.comments.util.PostCommentsSignaler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditPostCommentFragmentViewModel @Inject constructor(
    private val postCommentsService: PostCommentsService,
    private val postCommentsSignaler: PostCommentsSignaler,
    private val navMan: NavMan
) : BaseViewModel() {
    // properties
    var comment: PostComment? = null

    val content = MutableLiveData("")
    val charactersLeft = MutableLiveData(calculateCharactersLeft().toString())

    val isLocked = MutableLiveData(false)

    // functions
    fun calculateCharactersLeft(): Int {
        return CommentsConstants.POST_COMMENT_MAX_LENGTH - (content.value?.length ?: 0)
    }

    fun handleOnTapEdit() {
        edit()
    }

    private fun edit() {
        val comment = comment ?: return
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
            val dto = EditPostCommentDto(content)

            try {
                val postComment = postCommentsService.editPostComment(comment.id, dto)

                isLocked.value = false
                state.value = PageState.Idle

                postCommentsSignaler.send(PostCommentsSignaler.PostCommentSignal.PostCommentDidChange(postComment))
                navMan.popFragment()
            } catch (e: Throwable) {
                isLocked.value = false
                state.value = PageState.Idle
                toastMessage.value = e.message
            }
        }
    }
}
