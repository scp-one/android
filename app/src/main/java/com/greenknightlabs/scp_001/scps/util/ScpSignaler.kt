package com.greenknightlabs.scp_001.scps.util

import com.greenknightlabs.scp_001.scps.models.Scp
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScpSignaler @Inject constructor() {
    // interfaces
    interface Listener {
        fun handleSignal(signal: ScpSignal)
    }

    // enums
    sealed class ScpSignal {
        data class ScpDidChange(val scp: Scp) : ScpSignal()
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

    fun send(signal: ScpSignal) {
        listeners.forEach { it.handleSignal(signal) }
    }

    fun reset() {
        listeners.clear()
    }
}
