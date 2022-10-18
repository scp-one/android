package com.greenknightlabs.scp_001.app.util

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.concurrent.schedule

@Singleton
class Queuey @Inject constructor() {
    // properties
    private val tasks = mutableMapOf<String, Handler>()

    // functions
    fun queue(fn: () -> Unit, id: String, delay: Long = 3000) {
        val existingTask = tasks[id]

        if (existingTask != null) {
            existingTask.removeCallbacksAndMessages(null)
            tasks.remove(id)
        } else {
            val task = Handler(Looper.getMainLooper())
            task.postDelayed({
                fn()
                tasks.remove(id)
            }, delay)
            tasks[id] = task
        }
    }
}
