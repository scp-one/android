package com.greenknightlabs.scp_001.comments.util

import com.greenknightlabs.scp_001.comments.models.PostComment
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostCommentsSignaler @Inject constructor() {
    // interface
    interface Listener {
        fun handleSignal(signal: PostCommentSignal)
    }

    // enums
    sealed class PostCommentSignal {
        data class PostCommentDidCreate(val comment: PostComment) : PostCommentSignal()
        data class PostCommentDidChange(val comment: PostComment) : PostCommentSignal()
        data class PostCommentDidDelete(val comment: PostComment) : PostCommentSignal()
    }

    // properties
    private val listeners = mutableListOf<Listener>()

    // functions
    fun add(listener: Listener) {
        listeners.add(listener)
    }

    fun remove(listener: Listener) {
        listeners.remove(listener)
    }

    fun send(signal: PostCommentSignal) {
        listeners.forEach { it.handleSignal(signal) }
    }

    fun reset() {
        listeners.clear()
    }
}
