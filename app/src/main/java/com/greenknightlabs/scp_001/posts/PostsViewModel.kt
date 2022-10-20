package com.greenknightlabs.scp_001.posts

import com.greenknightlabs.scp_001.app.view_models.PageViewModel
import com.greenknightlabs.scp_001.posts.models.Post
import com.greenknightlabs.scp_001.posts.view_holders.PostComponentViewHolder

abstract class PostsViewModel : PageViewModel<Post>(), PostComponentViewHolder.Listener { }
