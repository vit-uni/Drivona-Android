package com.drivona.speed.utils

import com.drake.net.utils.withMain
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class TimerJob(private val totalDuration: Long, private val interval: Long) {
    private var job: Job? = null

    fun start(onTick: (Long) -> Unit, onFinish: () -> Unit) {
        job = GlobalScope.launch {
            var elapsedTime = 0L
            while (elapsedTime < totalDuration && isActive) {
                delay(interval)
                elapsedTime += interval
                withMain { onTick(elapsedTime) }
            }
            onFinish()
        }
    }

    fun cancel() {
        job?.cancel()
    }

    val isActive: Boolean
        get() = job?.isActive ?: false
}