package com.greenknightlabs.scp_001.posts.fragments.local_posts_fragment

import androidx.lifecycle.MutableLiveData
import com.greenknightlabs.scp_001.app.fragments.base_fragment.BaseViewModel
import com.greenknightlabs.scp_001.posts.models.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LocalPostsFragmentViewModel @Inject constructor() : BaseViewModel() {
    // properties
    val activityTitle = MutableLiveData<String?>(null)
    val posts = MutableLiveData<List<Post>?>(null)
    val webViewUrl = MutableLiveData<String?>(null)
    val shouldShowWebView = MutableLiveData(false)
}
