package com.hjq.widget.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import androidx.annotation.Nullable
import com.hjq.widget.CountdownTimer
import com.hjq.widget.R
import kotlin.math.max

class CountdownCircleProgressView @JvmOverloads constructor(
    context: Context,
    @Nullable attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): CircleProgressView(context, attrs, defStyleAttr) {

    var maxDuration: Int
    var countdownTimer: CountdownTimer? = null
    var maxProcess: Int = 0
    var countdownCallback: () -> Unit = {}
    init {
        @SuppressLint("Recycle") val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.CountdownCircleProgressView)
        maxDuration = typedArray.getInteger(R.styleable.CountdownCircleProgressView_maxDuration, 100) * 1000
        progress = maxDuration
        maxProgress = maxDuration
        maxProcess = maxDuration
        countdownTimer = CountdownTimer(maxDuration.toLong(), 40, 40,  {
            (context as Activity).runOnUiThread {
                maxProcess = maxProcess.minus(40)
                progress = maxProcess
            }
        }) {
            (context as Activity).runOnUiThread {
                countdownCallback.invoke()
                visibility = GONE
            }
        }


    }

    fun startDuration() {
        maxProgress = maxDuration
        maxProcess = maxDuration
        progress = maxProcess
        countdownTimer?.resetTime(maxDuration.toLong())
        countdownTimer?.start()
    }

    fun stop() {
        visibility = GONE
        countdownTimer?.stop()
    }

    fun resetDuration() {
        maxProcess = maxDuration
        progress = maxDuration
        maxProgress = maxDuration
        countdownTimer?.resetTime(maxDuration.toLong())
    }
}