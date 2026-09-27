package com.drivona.speed.utils

import java.util.Timer
import kotlin.concurrent.scheduleAtFixedRate

class CountdownTimer(private val endTime: Long, val callback: () -> Unit) {
    private var timeLeft = endTime - System.currentTimeMillis()
    private lateinit var timer: Timer
    private var interval = 1000L

    fun start() {
        if (timeLeft <= 0) {
            onCountdownFinished()
            return
        }
        timer = Timer()
        timer.scheduleAtFixedRate(0, interval) {
            timeLeft -= interval
            if (timeLeft <= 0) {
                timer.cancel()
                onCountdownFinished()
            } else {
                onTick(timeLeft)
            }
        }
    }

    fun cancel() {
        timer.cancel()
    }

    fun onTick(millisUntilFinished: Long) {
        // 默认空实现,子类可以重写
    }

    fun onCountdownFinished() {
        // 默认空实现,子类可以重写
        callback.invoke()
    }
}