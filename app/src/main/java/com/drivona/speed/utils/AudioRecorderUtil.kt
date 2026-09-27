package com.drivona.speed.utils

import android.content.Context
import android.media.MediaRecorder
import android.os.Environment
import java.io.File
import java.io.IOException

class AudioRecorderUtil(val context: Context, private val callback: AudioRecorderCallback) {
    private var mediaRecorder: MediaRecorder? = null
    private var audioFile: File? = null
    private var isRecording = false
    private var startTime: Long = 0

    interface AudioRecorderCallback {
        fun onRecordingStarted()
        fun onRecordingStopped(audioFile: File?, duration: Long)
        fun onRecordingTimeElapsed(time: Long)
    }

    fun startRecording() {
        if (isRecording) {
            return
        }
        val dir = File(Environment.getExternalStorageDirectory(), "MyApp")
        if (!dir.exists()) {
            dir.mkdir();
        }
        try {
            audioFile = createAudioFile()
            mediaRecorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(audioFile?.absolutePath)
                setMaxDuration(15000)
                prepare()
                start()
            }
            isRecording = true
            startTime = System.currentTimeMillis()
            callback.onRecordingStarted()
            startTimer()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun stopRecording() {
        if (!isRecording) {
            return
        }

        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null
            isRecording = false
            val duration = System.currentTimeMillis() - startTime
            callback.onRecordingStopped(audioFile, duration)
            stopTimer()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun createAudioFile(): File {
        val audioDir = File("${context.externalCacheDir}/sdcard/AudioRecorder")
        if (!audioDir.exists()) {
            audioDir.mkdirs()
        }
        return File(audioDir, "recording_${System.currentTimeMillis()}.mp3")
    }

    private fun startTimer() {
        Thread {
            while (isRecording) {
                try {
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                callback.onRecordingTimeElapsed(System.currentTimeMillis() - startTime)
            }
        }.start()
    }

    private fun stopTimer() {
        Thread.currentThread().interrupt()
    }
}