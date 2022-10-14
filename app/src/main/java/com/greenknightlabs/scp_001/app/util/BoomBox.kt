package com.greenknightlabs.scp_001.app.util

import android.content.Context
import android.graphics.drawable.Drawable
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.greenknightlabs.scp_001.R
import timber.log.Timber
import java.lang.ref.WeakReference
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt

@Singleton
class BoomBox @Inject constructor(
    private val context: Context
) {
    // enums
    enum class PlayingStatus {
        Playing,
        Paused,
        Buffering,
        Ended,
        Error
    }

    // properties
    private val mediaPlayer: MediaPlayer = MediaPlayer()
    private val handler = Handler(Looper.getMainLooper())

    private val _url = MutableLiveData<String?>(null)
    val url: LiveData<String?> get() = _url

    private val playingStatus = MutableLiveData(PlayingStatus.Paused)
    val progress = MutableLiveData(0)

    private var imageView: WeakReference<ImageView>? = null
    private var progressBar: WeakReference<ProgressBar>? = null

    init {
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
    }

    // functions
    fun play(imageView: ImageView, progressBar: ProgressBar, url: String) {
        if (this.url.value != url) {
            resetViews()
            this.imageView = WeakReference(imageView)
            this.progressBar = WeakReference(progressBar)
            resetAndPlay(this.progressBar, url)
        } else {
            when (playingStatus.value) {
                PlayingStatus.Playing -> {
                    pause()
                }
                PlayingStatus.Paused -> {
                    resume(this.progressBar, url)
                }
                PlayingStatus.Buffering -> {
                    // do nothing
                }
                PlayingStatus.Ended -> {
                    mediaPlayer.seekTo(0)
                    resume(this.progressBar, url)
//                    resetAndPlay(progressBar, url)
                }
                PlayingStatus.Error -> {
                    // TODO: something?
                }
                null -> {
                    // should not happen
                }
            }
        }

        this.imageView?.get()?.setImageDrawable(determineImageViewDrawable(url))
    }

    private fun resetAndPlay(progressBar: WeakReference<ProgressBar>?, url: String) {
        _url.value = url

        try {
            mediaPlayer.reset()
            mediaPlayer.setDataSource(url)
            mediaPlayer.prepare()
            mediaPlayer.start()
            startTimer(progressBar)
        } catch (e: Throwable) {
            Timber.e(e)
            playingStatus.value = PlayingStatus.Error
        }
    }

    private fun resume(progressBar: WeakReference<ProgressBar>?, url: String) {
        mediaPlayer.start()
        startTimer(progressBar)
    }

    fun determineImageViewDrawable(url: String): Drawable {
        if (_url.value != url) {
            return context.resources.getDrawable(R.drawable.ic_play)
        }

        return when (playingStatus.value) {
            PlayingStatus.Paused -> context.resources.getDrawable(R.drawable.ic_play)
            PlayingStatus.Playing -> context.resources.getDrawable(R.drawable.ic_pause)
            PlayingStatus.Buffering -> context.resources.getDrawable(R.drawable.ic_not_started)
            PlayingStatus.Ended -> context.resources.getDrawable(R.drawable.ic_replay)
            PlayingStatus.Error -> context.resources.getDrawable(R.drawable.ic_cancel)
            null -> context.resources.getDrawable(R.drawable.ic_cancel)
        }
    }

    fun restoreViews(imageView: ImageView, progressBar: ProgressBar, url: String): Int {
        if (_url.value != url) { return 0 }

        this.imageView = WeakReference(imageView)
        this.progressBar = WeakReference(progressBar)
        startTimer(this.progressBar)

        return this.progress.value ?: 0
    }

    private fun resetViews() {
        removeProgressUpdater()
        val icon = context.resources.getDrawable(R.drawable.ic_play)
        this.imageView?.get()?.setImageDrawable(icon)
        this.progressBar?.get()?.progress = 0
    }

    private fun startTimer(progressBar: WeakReference<ProgressBar>?) {
        playingStatus.value = PlayingStatus.Playing

        val runnableCode: Runnable = object : Runnable {
            override fun run() {
                progress.value = (mediaPlayer.currentPosition.toFloat() / mediaPlayer.duration.toFloat() * 100).roundToInt()
                progressBar?.get()?.progress = progress.value!!

                Timber.d("progress: ${progress.value.toString()}")

                if ((progress.value ?: 0) >= 100) {
                    onAudioEnded()
                } else {
                    handler.postDelayed(this, 1000)
                }
            }
        }

        handler.post(runnableCode)
    }

    private fun onAudioEnded() {
        removeProgressUpdater()
        playingStatus.value = PlayingStatus.Ended

        val icon = context.resources.getDrawable(R.drawable.ic_replay)
        this.imageView?.get()?.setImageDrawable(icon)
    }

    private fun pause() {
        mediaPlayer.pause()
        removeProgressUpdater()
        playingStatus.value = PlayingStatus.Paused
    }

    private fun removeProgressUpdater() {
        handler.removeCallbacksAndMessages(null)
    }
}
