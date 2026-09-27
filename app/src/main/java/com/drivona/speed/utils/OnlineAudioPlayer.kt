package com.drivona.speed.utils

import android.media.MediaPlayer
import android.os.Handler

class OnlineAudioPlayer() {
    private val mediaPlayer = MediaPlayer()
    private var isPrepared = false
    private var isPaused = false
    private var isStopped = false
    private var progressHandler: Handler? = null
    private var progressRunnable: Runnable? = null
    private var onPreparedListener: ((duration: Int) -> Unit)? = null
    private var onCompletionListener: (() -> Unit)? = null
    private var onErrorListener: ((errorCode: Int) -> Unit)? = null

    fun prepare(url: String) {
        try {
            mediaPlayer.setDataSource(url)
            mediaPlayer.setOnPreparedListener {
                isPrepared = true
                onPreparedListener?.invoke(mediaPlayer.duration)
                if (!isPaused) {
                    mediaPlayer.start()
                }
            }
            mediaPlayer.setOnCompletionListener {
                onCompletionListener?.invoke()
            }
            mediaPlayer.setOnErrorListener { _, what, extra ->
                onErrorListener?.invoke(what)
                false
            }
            mediaPlayer.prepareAsync()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun play(url: String) {
        if (isStopped) {
            prepare(url)
        } else if (isPaused) {
            isPaused = false
            mediaPlayer.start()
        }
        startProgressHandler()
    }

    fun pause() {
        if (isPrepared && mediaPlayer.isPlaying) {
            isPaused = true
            mediaPlayer.pause()
            stopProgressHandler()
        }
    }

    fun stop() {
        if (isPrepared || isPaused) {
            isStopped = true
            isPaused = false
            isPrepared = false
            mediaPlayer.stop()
            mediaPlayer.reset()
            stopProgressHandler()
        }
    }

    fun onDestroy(){
        if (isPrepared || isPaused) {
            isStopped = true
            isPaused = false
            isPrepared = false
            mediaPlayer.stop()
            mediaPlayer.reset()
            mediaPlayer.release()
            stopProgressHandler()
        }
    }

    fun seekTo(position: Int) {
        if (isPrepared) {
            mediaPlayer.seekTo(position)
        }
    }

    fun getCurrentPosition(): Int {
        return if (isPrepared) {
            mediaPlayer.currentPosition
        } else {
            0
        }
    }

    /**
     * 播放器准备完成后回调，duration 为音频总时长
     */
    fun setOnPreparedListener(listener: (duration: Int) -> Unit) {
        onPreparedListener = listener
    }

    /**
     * 播放完成回调
     */
    fun setOnCompletionListener(listener: () -> Unit) {
        onCompletionListener = listener
    }

    /**
     * 播放出错回调，errorCode 为错误码
     */
    fun setOnErrorListener(listener: (errorCode: Int) -> Unit) {
        onErrorListener = listener
    }

    private fun startProgressHandler() {
        progressHandler = Handler()
        progressRunnable = object : Runnable {
            override fun run() {
                onProgressListener?.invoke(getCurrentPosition())
                progressHandler?.postDelayed(this, 1000)
            }
        }
        progressHandler?.post(progressRunnable!!)
    }

    private fun stopProgressHandler() {
        progressHandler?.removeCallbacks(progressRunnable!!)
        progressHandler = null
        progressRunnable = null
    }

    private var onProgressListener: ((position: Int) -> Unit)? = null

    /**
     * 播放进度回调，position 为当前播放位置
     */
    fun setOnProgressListener(listener: (position: Int) -> Unit) {
        onProgressListener = listener
    }

}