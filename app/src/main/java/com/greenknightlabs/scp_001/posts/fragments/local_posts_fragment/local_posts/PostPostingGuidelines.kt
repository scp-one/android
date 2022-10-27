package com.greenknightlabs.scp_001.posts.fragments.local_posts_fragment.local_posts

import com.greenknightlabs.scp_001.posts.enums.PostStatus
import com.greenknightlabs.scp_001.posts.enums.PostVisibility
import com.greenknightlabs.scp_001.posts.models.Post
import com.greenknightlabs.scp_001.users.models.UserRef

fun getPostPostingGuidelines(): Post {
    return Post(
        "1",
        UserRef(false, "123", null),
        "",
        """
        ## Posting
        - It is advised not to post personal information
        - If posting existing content, please make sure you have permission to do so
        - Posts should be well formatted

        ## Approval
        - Posts are subject to approval before other users can see them
        - As long as you have followed the guidelines, your post should be approved within a day or two

        ## Rejections
        Your post can be rejected for the following reasons:
        - Content is unrelated
        - Inappropriate content
        - Improper use of formatting

        If your post is rejected, verify that your content is following the guidelines, make an edit and re-submit for approval
        """.trimIndent(),
        null,
        PostVisibility.VISIBLE,
        PostStatus.APPROVED,
        null,
        0,
        0,
        null,
        "123",
        "123",
        false
    )
}
