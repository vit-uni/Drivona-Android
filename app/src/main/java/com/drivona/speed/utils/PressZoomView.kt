package com.drivona.speed.utils

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class PressZoomView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var isZoomedIn = false
    private var defaultScale = 1.0f
    private var zoomedInScale = 1.2f

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // 在画布上绘制一个圆形
        val radius = 50f
        val x = width / 2f
        val y = height / 2f
        val scale = if (isZoomedIn) zoomedInScale else defaultScale
        canvas.save()
        canvas.scale(scale, scale, x, y)
        canvas.drawCircle(x, y, radius, paint)
        canvas.restore()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (isZoomedIn) {
                    // 如果已经放大过，则不响应按下事件，否则会一直保持放大状态
                    return false
                }
                zoomIn()
                return true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (isZoomedIn) {
                    zoomOut()
                }
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    private fun zoomIn() {
        // 使用 ObjectAnimator 使视图放大
        val scaleX = ObjectAnimator.ofFloat(this, View.SCALE_X, defaultScale, zoomedInScale)
        val scaleY = ObjectAnimator.ofFloat(this, View.SCALE_Y, defaultScale, zoomedInScale)

        // 使用 AnimatorSet 同时播放 X 和 Y 的动画，将持续 200 毫秒
        val animatorSet = AnimatorSet()
        animatorSet.duration = 200
        animatorSet.playTogether(scaleX, scaleY)
        animatorSet.start()

        isZoomedIn = true
    }

    private fun zoomOut() {
        // 使用 ObjectAnimator 使视图还原
        val scaleX = ObjectAnimator.ofFloat(this, View.SCALE_X, zoomedInScale, defaultScale)
        val scaleY = ObjectAnimator.ofFloat(this, View.SCALE_Y, zoomedInScale, defaultScale)

        // 使用 AnimatorSet 同时播放 X 和 Y 的动画，将持续 200 毫秒
        val animatorSet = AnimatorSet()
        animatorSet.duration = 200
        animatorSet.playTogether(scaleX, scaleY)
        animatorSet.start()

        isZoomedIn = false
    }
}