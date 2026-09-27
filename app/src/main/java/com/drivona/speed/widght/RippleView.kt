package com.drivona.speed.widght

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.content.ContextCompat
import com.drivona.speed.R

class RippleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val ripplePaint: Paint = Paint().apply {
        isAntiAlias = true
    }
    private var rippleColor: Int = Color.BLUE
    private var rippleAlpha: Int = 255
    private var rippleRadius: Int = 0
    private var maxRippleRadius: Int = 200
    private var rippleDuration: Long = 2000
    private var centerX: Int = 0
    private var centerY: Int = 0
    private var isRippleAnimRunning: Boolean = false
    private var rippleWidth: Int = 5 // 默认圆环宽度为10
    private var rippleSpacing: Int = 5 // 默认圆环宽度为10
    private var rippleCount: Int = 3 // 默认圆环数量为3
    private var totalWidth: Int = 0 // 圆环序列的总宽度
    private var animator: ValueAnimator? = null
    init {
        context.obtainStyledAttributes(attrs, R.styleable.RippleView).apply {
            rippleColor = getColor(
                R.styleable.RippleView_rippleColor,
                ContextCompat.getColor(context, R.color.default_ripple_color)
            )
            rippleAlpha = getInt(R.styleable.RippleView_rippleAlpha, 255)
            rippleDuration = getInt(R.styleable.RippleView_rippleDuration, 2000).toLong()
            rippleCount = getInt(R.styleable.RippleView_rippleCount, 3)
            rippleSpacing = getInt(R.styleable.RippleView_rippleSpacing, 10)
            recycle()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        totalWidth = rippleCount * (2 * rippleWidth + rippleSpacing) - rippleSpacing
        val desiredWidth = totalWidth + paddingLeft + paddingRight
        val desiredHeight = MeasureSpec.getSize(heightMeasureSpec) + paddingTop + paddingBottom

        setMeasuredDimension(
            resolveSize(desiredWidth, widthMeasureSpec),
            resolveSize(desiredHeight, heightMeasureSpec)
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        ripplePaint.style = Paint.Style.STROKE
        ripplePaint.strokeWidth = rippleWidth.toFloat()
//
//        val startOffset = (width - totalWidth) / 2 // 起始偏移量，使圆环序列居中
//
//        for (i in 0 until rippleCount) {
//            val currentRadius = rippleRadius + i * (rippleWidth * 2 + rippleSpacing) // 根据索引计算圆环半径
//            val currentAlpha = (rippleAlpha * (1 - i.toFloat() / rippleCount)).toInt() // 根据索引计算圆环透明度
//            val currentOffset = i * (rippleWidth * 2 + rippleSpacing) + rippleWidth + startOffset // 当前圆环的偏移量，包括起始偏移量和圆环宽度的一半
//
//            ripplePaint.color = rippleColor
//            ripplePaint.alpha = currentAlpha
//            canvas.drawCircle(
//                currentOffset.toFloat(),
//                centerY.toFloat(),
//                currentRadius.toFloat(),
//                ripplePaint
//            )
//        }

        for (i in 0 until rippleCount) {
            val currentRadius = rippleRadius + i * rippleWidth * 2 // 根据索引计算圆环半径
            val currentAlpha = (rippleAlpha * (1 - i.toFloat() / rippleCount)).toInt() // 根据索引计算圆环透明度

            ripplePaint.color = rippleColor
            ripplePaint.alpha = currentAlpha
            canvas.drawCircle(centerX.toFloat(), centerY.toFloat(), currentRadius.toFloat(), ripplePaint)
        }
    }

    fun startRippleAnimation() {
        if (!isRippleAnimRunning) {
            val animatorList = ArrayList<ValueAnimator>()
            val delayIncrement = 500 // 每个圆环动画的延迟时间增量

//            for (i in 0 until rippleCount) {
            animator = ValueAnimator.ofInt(0, maxRippleRadius / 2).apply {
                    duration = rippleDuration
                    interpolator = AccelerateDecelerateInterpolator()
                    repeatCount = ValueAnimator.INFINITE
                    repeatMode = ValueAnimator.RESTART
                    addUpdateListener { animation ->
                        rippleRadius = animation.animatedValue as Int
                        invalidate()
                    }
                    addListener(object : Animator.AnimatorListener {

                        override fun onAnimationStart(p0: Animator) {
                            isRippleAnimRunning = true
                        }

                        override fun onAnimationEnd(p0: Animator) {
                            isRippleAnimRunning = false
                            rippleRadius = 0
                            invalidate()
                        }

                        override fun onAnimationCancel(p0: Animator) {
                            isRippleAnimRunning = false
                        }

                        override fun onAnimationRepeat(p0: Animator) {

                        }
                    })
//                    startDelay = (i * delayIncrement).toLong() // 设置每个圆环动画的延迟时间
                    start()
                }

//                animatorList.add(animator)
//            }
            animator?.start()
//            animatorList.forEach { it.start() }
        }
    }

    fun stopAnim() {
        animator?.cancel()
    }

    fun setRippleColor(color: Int) {
        rippleColor = color
        invalidate()
    }

    fun setRippleAlpha(alpha: Int) {
        rippleAlpha = alpha
        invalidate()
    }

    fun setRippleDuration(duration: Long) {
        rippleDuration = duration
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        centerX = w / 2
        centerY = h / 2
    }
}
