package com.greenknightlabs.scp_001.app.util

import android.media.AudioManager
import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BoomBox @Inject constructor() {
    // properties
    private val mediaPlayer: MediaPlayer = MediaPlayer()

    private val _url = MutableLiveData<String?>(null)
    val url: LiveData<String?> get() = _url

    init {
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
    }

    // functions
    fun play(url: String) {
        if (this.url.value != url) {
            _url.value = url

            try {
                mediaPlayer.setDataSource(url)
                mediaPlayer.prepare()
                mediaPlayer.start()
            } catch (e: Throwable) {
                Timber.e(e)
            }
        } else {
            mediaPlayer.start()
        }
    }

    fun pause() {
        mediaPlayer.pause()
    }
}
