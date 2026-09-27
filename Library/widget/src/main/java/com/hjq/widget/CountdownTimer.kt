package com.hjq.widget

import java.util.*

class CountdownTimer(
    private var totalTimeInSeconds: Long,
    private var delay: Long = 1000,
    private var period: Long = 1000,
    private val onTick: (String) -> Unit,
    private val onCountdownFinish: () -> Unit
) {
    private var timer: Timer? = null

    init {
        if (totalTimeInSeconds < 0) {
            throw IllegalArgumentException("Total time must be a non-negative value.")
        }
    }

    fun start() {
        timer = Timer()
        timer?.scheduleAtFixedRate(object : TimerTask() {

            override fun run() {
                if (totalTimeInSeconds > 0) {
                    val remainingTime = totalTimeInSeconds
                    val formattedTime = formatTime(remainingTime)
                    onTick.invoke(formattedTime)
                    totalTimeInSeconds = totalTimeInSeconds.minus(delay)
                } else {
                    stop()
                    onCountdownFinish.invoke()
                }
            }
        }, delay, period)
    }

    fun stop() {
        timer?.cancel()
        timer?.purge()
    }

    fun addTime(secondsToAdd: Long) {
        if (secondsToAdd >= 0) {
            totalTimeInSeconds += secondsToAdd
            val formattedTime = formatTime(totalTimeInSeconds)
            onTick.invoke(formattedTime)
        }
    }

    fun subtractTime(secondsToSubtract: Long) {
        if (secondsToSubtract >= 0 && totalTimeInSeconds >= secondsToSubtract) {
            totalTimeInSeconds -= secondsToSubtract
            val formattedTime = formatTime(totalTimeInSeconds)
            onTick.invoke(formattedTime)
        }
    }

    private fun formatTime(timeInSeconds: Long): String {
        val hours = timeInSeconds / 3600 / 1000
        val minutes = timeInSeconds / 1000 % 3600 / 60
        val seconds = timeInSeconds / 1000 % 60

        return if (hours > 0) {
            String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
        }
    }

    fun resetTime(time: Long) {
        totalTimeInSeconds = time
    }

}
