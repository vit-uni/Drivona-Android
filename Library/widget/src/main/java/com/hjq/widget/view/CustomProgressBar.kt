package com.hjq.widget.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.hjq.widget.R

class CustomProgressBar(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val backgroundPaint = Paint()
    private val progressPaint = Paint()

    private var maxProgress = 100
    private var currentProgress = 50

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomProgressBar)

        val backgroundColor = typedArray.getColor(
            R.styleable.CustomProgressBar_backgroundColor,
            Color.GRAY
        )
        val progressColor = typedArray.getColor(
            R.styleable.CustomProgressBar_cpb_progressColor,
            Color.GREEN
        )
        maxProgress = typedArray.getInt(R.styleable.CustomProgressBar_maxProgress, 100)
        currentProgress = typedArray.getInt(R.styleable.CustomProgressBar_currentProgress, 50)

        backgroundPaint.color = backgroundColor
        progressPaint.color = progressColor

        typedArray.recycle()
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val backgroundRect = RectF(0f, 0f, width.toFloat(), height.toFloat())
        canvas.drawRect(backgroundRect, backgroundPaint)

        val progressWidth = (currentProgress.toFloat() / maxProgress) * width
        val progressRect = RectF(0f, 0f, progressWidth, height.toFloat())
        canvas.drawRect(progressRect, progressPaint)
    }

    fun setMaxProgress(max: Int) {
        maxProgress = max
        invalidate()
    }

    fun setCurrentProgress(progress: Int) {
        currentProgress = progress
        invalidate()
    }
}

