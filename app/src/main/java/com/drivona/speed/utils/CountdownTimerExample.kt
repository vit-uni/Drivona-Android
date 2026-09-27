package com.drivona.speed.utils

import android.os.CountDownTimer
import android.widget.TextView

class CountdownTimerExample(var text: TextView, var format: String) {

    fun startCountdown(milliseconds: Long, interval: Long = 1000, callBack: (Long) -> Unit = {}) {
        val timer = object : CountDownTimer(milliseconds, interval) {
            override fun onTick(millisUntilFinished: Long) {
                // 在倒计时进行中执行的操作
                val seconds = millisUntilFinished / 1000
                println("Remaining Time: $seconds seconds")
            }

            override fun onFinish() {
                // 倒计时完成后执行的操作
                println("Countdown Finished")
            }
        }

        timer.start()
    }
}