package com.greenknightlabs.scp_001.posts.util

import com.greenknightlabs.scp_001.posts.models.Post
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostSignaler @Inject constructor() {
    // interface
    interface Listener {
        fun handleSignal(signal: PostSignal)
    }

    // enums
    sealed class PostSignal {
        data class PostDidChange(val post: Post) : PostSignal()
        data class PostDidDelete(val post: Post) : PostSignal()
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

    fun send(signal: PostSignal) {
        listeners.forEach { it.handleSignal(signal) }
    }

    fun reset() {
        listeners.clear()
    }
}
