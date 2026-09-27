package com.hjq.widget.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.widget.TextView

class ScalingTextView(context: Context, attrs: AttributeSet) : TextView(context, attrs) {

    private var scaleAnimator: ValueAnimator? = null
    private var scaleRate = 2f

    fun setScaleRate(rate: Float) {
        scaleRate = rate
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        super.setText(text, type)

        // 取消之前的动画
        scaleAnimator?.cancel()

        // 创建并开始新的动画
        scaleAnimator = ValueAnimator.ofFloat(1.0f, scaleRate).apply {
            addUpdateListener { animation ->
                val scale = animation.animatedValue as Float
                scaleX = scale
                scaleY = scale
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    resetScale()
                }
            })
            start()
        }
    }

    fun resetScale() {
        val resetAnimator = ValueAnimator.ofFloat(scaleX, 1.0f)
        resetAnimator.addUpdateListener { animation ->
            val scale = animation.animatedValue as Float
            scaleX = scale
            scaleY = scale
        }
        resetAnimator.start()
    }
}