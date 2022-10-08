package com.greenknightlabs.scp_001.posts.fragments.post_fragment

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.greenknightlabs.scp_001.app.extensions.makePopupMenu
import com.greenknightlabs.scp_001.app.fragments.base_fragment.BaseViewModel
import com.greenknightlabs.scp_001.app.util.NavMan
import com.greenknightlabs.scp_001.posts.interfaces.PostAuthorComponentListener
import com.greenknightlabs.scp_001.posts.models.Post
import com.greenknightlabs.scp_001.users.fragments.user_profile_fragment.UserProfileFragment
import com.greenknightlabs.scp_001.users.models.User
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PostFragmentViewModel @Inject constructor(
    private val navMan: NavMan
) : BaseViewModel(), PostAuthorComponentListener {
    // properties
    val post = MutableLiveData<Post?>(null)

    // functions
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