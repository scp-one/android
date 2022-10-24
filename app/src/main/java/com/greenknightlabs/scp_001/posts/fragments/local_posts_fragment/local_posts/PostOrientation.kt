package com.greenknightlabs.scp_001.posts.fragments.local_posts_fragment.local_posts

import com.greenknightlabs.scp_001.posts.enums.PostStatus
import com.greenknightlabs.scp_001.posts.enums.PostVisibility
import com.greenknightlabs.scp_001.posts.models.Post
import com.greenknightlabs.scp_001.users.models.UserRef

fun getPostOrientation(): Post {
    return Post(
        "1",
        UserRef(false, "123", null),
        "Orientation",
        """
        The SCP Foundation is a fictional secret organization documented by the collaborative writing wiki project of the same name. Within the website's shared universe, the Foundation is responsible for capturing and containing various paranormal, supernatural, and other mysterious phenomena unexplained by mainstream science (known as "anomalies" or "SCPs"), while also keeping their existence hidden from the rest of human society.

        The real-world website is community-based and includes elements of many genres such as horror, science fiction, and urban fantasy. The majority of works consist of thousands of SCP files, confidential quasi-scientific reports that document an SCP object and the means of keeping it contained.
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
