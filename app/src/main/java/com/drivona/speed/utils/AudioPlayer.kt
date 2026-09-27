package com.drivona.speed.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import java.io.IOException

class AudioPlayer(private val context: Context)  {
    private var mediaPlayer: MediaPlayer? = null
    private var playbackPosition = 0

    fun playAudioFile(filePath: String) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build())
                setOnCompletionListener {
                    stopPlayback()
                }
            }
        }

        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                it.reset()
                it.setDataSource(context, Uri.parse(filePath))
                it.prepare()
                it.start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stopPlayback() {
        mediaPlayer?.let {
            it.stop()
            it.release()
            mediaPlayer = null
        }
    }

    fun pausePlayback() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                playbackPosition = it.currentPosition
            }
        }
    }

    fun resumePlayback() {
        mediaPlayer?.let {
            if (!it.isPlaying) {
                it.seekTo(playbackPosition)
                it.start()
            }
        }
    }

    fun play(url: String, callback: (Int) -> Unit) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build())
            setDataSource(url)
            prepareAsync()
            setOnCompletionListener {
                callback.invoke(2)
                stopPlayback()
            }
            setOnPreparedListener {
                callback.invoke(1)
                start()
            }
        }
    }

    fun getAudioDuration(url: String, callback: (Int) -> Unit) {
        val mediaPlayer = MediaPlayer()

        try {
            mediaPlayer.setDataSource(url)
            mediaPlayer.setOnPreparedListener { player ->
                val duration = player.duration
                callback(duration)
            }
            mediaPlayer.setOnErrorListener { player, _, _ ->
                callback(0)
                false
            }
            mediaPlayer.setOnCompletionListener {
                callback(0)
            }
            mediaPlayer.prepareAsync()
        } catch (e: IOException) {
            e.printStackTrace()
            callback(0)
        }

        // 设置超时时间，避免某些情况下无法正常获取音频时长
        Handler().postDelayed({
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
                callback(0)
            }
        }, 10000) // 10秒钟超时时间，根据实际情况进行调整
    }
}