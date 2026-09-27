package com.hjq.widget.view;

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.annotation.ColorRes
import androidx.annotation.Nullable
import androidx.core.content.ContextCompat
import com.hjq.widget.R


/**
 * date:2021/1/4 0004
 * author:wsm (Administrator)
 * funcation:圆形进度条控件
 */
open class CircleProgressView @JvmOverloads constructor(
    context: Context,
    @Nullable attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val mBackPaint: Paint
    private val mProgPaint // 绘制画笔
            : Paint
    private var mRectF: RectF? = null // 绘制区域
    private var mColorArray // 圆环渐变色
            : IntArray?
    private var mProgress // 圆环进度(0-100)
            : Int
    private var mMaxProcess: Int = 100
    private var isClockwise: Boolean = true
    private var progressMode: Int = 1

    init {
        @SuppressLint("Recycle") val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.CircleProgressView)
        mMaxProcess = typedArray.getInteger(R.styleable.CircleProgressView_max_progress, 100)
        isClockwise = typedArray.getBoolean(R.styleable.CircleProgressView_is_clockwise, true)
        progressMode = typedArray.getInteger(R.styleable.CircleProgressView_progress_mode, 1)
        // 初始化背景圆环画笔
        mBackPaint = Paint()
        mBackPaint.style = Paint.Style.STROKE // 只描边，不填充
        mBackPaint.strokeCap = Paint.Cap.ROUND // 设置圆角
        mBackPaint.isAntiAlias = true // 设置抗锯齿
        mBackPaint.isDither = true // 设置抖动
        mBackPaint.strokeWidth =
            typedArray.getDimension(R.styleable.CircleProgressView_backWidth, 5f)
        mBackPaint.color =
            typedArray.getColor(R.styleable.CircleProgressView_backColor, Color.LTGRAY)

        // 初始化进度圆环画笔
        mProgPaint = Paint()
        mProgPaint.style = Paint.Style.STROKE // 只描边，不填充
        mProgPaint.strokeCap = Paint.Cap.ROUND // 设置圆角
        mProgPaint.isAntiAlias = true // 设置抗锯齿
        mProgPaint.isDither = true // 设置抖动
        mProgPaint.isAntiAlias = true;
        mProgPaint.strokeWidth =
            typedArray.getDimension(R.styleable.CircleProgressView_progWidth, 10f)
        mProgPaint.color =
            typedArray.getColor(R.styleable.CircleProgressView_progColor, Color.BLUE)

        // 初始化进度圆环渐变色
        val startColor = typedArray.getColor(R.styleable.CircleProgressView_progStartColor, -1)
        val firstColor = typedArray.getColor(R.styleable.CircleProgressView_progFirstColor, -1)
        mColorArray =
            if (startColor != -1 && firstColor != -1) intArrayOf(startColor, firstColor) else null

        // 初始化进度
        mProgress = typedArray.getInteger(R.styleable.CircleProgressView_progress, 0)
        typedArray.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val viewWide = measuredWidth - paddingLeft - paddingRight
        val viewHigh = measuredHeight - paddingTop - paddingBottom
        val mRectLength =
            ((if (viewWide > viewHigh) viewHigh else viewWide) - if (mBackPaint.strokeWidth > mProgPaint.strokeWidth) mBackPaint.strokeWidth else mProgPaint.strokeWidth).toInt()
        val mRectL = paddingLeft + (viewWide - mRectLength) / 2
        val mRectT = paddingTop + (viewHigh - mRectLength) / 2
        mRectF = RectF(
            mRectL.toFloat(),
            mRectT.toFloat(),
            (mRectL + mRectLength).toFloat(),
            (mRectT + mRectLength).toFloat()
        )

        // 设置进度圆环渐变色
        if (mColorArray != null && mColorArray!!.size > 1) mProgPaint.shader =
            LinearGradient(
                0f, 0f, 0f, measuredWidth.toFloat(),
                mColorArray!!, null, Shader.TileMode.MIRROR
            )
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if(progressMode == 1) {
            val startAngle = if (isClockwise) 0f else 360f
            val sweepAngle = if (isClockwise) (360 * mProgress / mMaxProcess).toFloat() else -(360 * mProgress / mMaxProcess).toFloat()
            canvas.drawArc(mRectF!!, 0f, 360f, false, mBackPaint)
            canvas.drawArc(mRectF!!, startAngle, sweepAngle, false, mProgPaint)
        } else {
            val viewWide = measuredWidth - paddingLeft - paddingRight
            val viewHigh = measuredHeight - paddingTop - paddingBottom
            val mRectLength =
                ((viewWide) - if (mBackPaint.strokeWidth > mProgPaint.strokeWidth) mBackPaint.strokeWidth else mProgPaint.strokeWidth).toInt()
            val mRectL = paddingLeft + (viewWide - mRectLength) / 2
            val mRectT = paddingTop + (viewHigh - mRectLength) / 2

            // 定义矩形边界，这里假设矩形位于 (0, 0) 坐标处
            val rectBounds = RectF(0f, 0f, width.toFloat(), height.toFloat())
            mProgPaint.style = Paint.Style.FILL
            mBackPaint.style = Paint.Style.FILL
//            canvas.drawRect(0.toFloat(), -height.toFloat(), width.toFloat(), height.toFloat(), mBackPaint)
            canvas.drawRoundRect(rectBounds, (height / 2).toFloat(), (height / 2).toFloat(), mBackPaint)
            val startAngle = if (isClockwise)  viewHigh.toFloat() else  width.toFloat()
            val sweepAngle = if (isClockwise) (mProgress * (width.toFloat() / mMaxProcess.toFloat())) else width - (mProgress * (width / mMaxProcess)).toFloat()
            val rectBounds2 = RectF(0.toFloat(), 0f, sweepAngle, height.toFloat())
            canvas.drawRoundRect(rectBounds2, (height / 2).toFloat(), (height / 2).toFloat(), mProgPaint)
        }
    }

    fun createPath(): Path {
        // 创建 Path 对象
        val path = Path()

        // 添加左上角圆角矩形部分
//        val topLeftRect = RectF(0f, 0f, rectWidth - cornerRadiusEndTR, cornerRadiusStartTL)
//        path.addRoundRect(topLeftRect, floatArrayOf(cornerRadiusStartTL, cornerRadiusStartTL), Path.Direction.CW)
//
//        // 添加右上角直角矩形部分
//        val topRightRect = RectF(rectWidth - cornerRadiusEndTR, 0f, rectWidth, cornerRadiusStartBR)
//        path.addRect(topRightRect, Path.Direction.CW)
//
//        // 添加右下角圆角矩形部分
//        val bottomRightRect = RectF(rectWidth - cornerRadiusEndTR, rectHeight - cornerRadiusStartBR, rectWidth, rectHeight)
//        path.addRoundRect(bottomRightRect, floatArrayOf(cornerRadiusEndBR, cornerRadiusEndBR), Path.Direction.CW)
//
//        // 添加左下角直角矩形部分
//        val bottomLeftRect = RectF(0f, rectHeight - cornerRadiusEndBL, rectWidth - cornerRadiusEndTR, rectHeight)
//        path.addRect(bottomLeftRect, Path.Direction.CW)

        // 结束构建路径
        path.close()
        return path
    }

    // ---------------------------------------------------------------------------------------------
    var progress: Int
        /**
         * 获取当前进度
         *
         * @return 当前进度（0-100）
         */
        get() = mProgress
        /**
         * 设置当前进度
         *
         * @param progress 当前进度（0-100）
         */
        set(progress) {
            mProgress = progress
            invalidate()
        }

    var maxProgress: Int
        /**
         * 最大进度
         *
         * @return 默认进度（0-100）
         */
        get() = mMaxProcess
        /**
         * 最大
         *
         * @param progress 默认进度（0-100）
         */
        set(maxProgress) {
            mMaxProcess = maxProgress
        }

    /**
     * 设置当前进度，并展示进度动画。如果动画时间小于等于0，则不展示动画
     *
     * @param progress 当前进度（0-100）
     * @param animTime 动画时间（毫秒）
     */
    fun setProgress(progress: Int, animTime: Long) {
        if (animTime <= 0) this.progress = progress else {
            val animator = ValueAnimator.ofInt(mProgress, progress)
            animator.addUpdateListener { animation ->
                mProgress = animation.animatedValue as Int
                invalidate()
            }
            animator.interpolator = OvershootInterpolator()
            animator.duration = animTime
            animator.start()
        }
    }

    /**
     * 设置背景圆环宽度
     *
     * @param width 背景圆环宽度
     */
    fun setBackWidth(width: Int) {
        mBackPaint.strokeWidth = width.toFloat()
        invalidate()
    }

    /**
     * 设置背景圆环颜色
     *
     * @param color 背景圆环颜色
     */
    fun setBackColor(@ColorRes color: Int) {
        mBackPaint.color = ContextCompat.getColor(context, color)
        invalidate()
    }

    /**
     * 设置进度圆环宽度
     *
     * @param width 进度圆环宽度
     */
    fun setProgWidth(width: Int) {
        mProgPaint.strokeWidth = width.toFloat()
        invalidate()
    }

    /**
     * 设置进度圆环颜色
     *
     * @param color 景圆环颜色
     */
    fun setProgColor(@ColorRes color: Int) {
        mProgPaint.color = ContextCompat.getColor(context, color)
        mProgPaint.shader = null
        invalidate()
    }

    /**
     * 设置进度圆环颜色(支持渐变色)
     *
     * @param startColor 进度圆环开始颜色
     * @param firstColor 进度圆环结束颜色
     */
    fun setProgColor(@ColorRes startColor: Int, @ColorRes firstColor: Int) {
        mColorArray = intArrayOf(
            ContextCompat.getColor(context, startColor), ContextCompat.getColor(
                context, firstColor
            )
        )
        mProgPaint.shader = LinearGradient(
            0f, 0f, 0f, measuredWidth.toFloat(),
            mColorArray!!, null, Shader.TileMode.MIRROR
        )
        invalidate()
    }

    /**
     * 设置进度圆环颜色(支持渐变色)
     *
     * @param colorArray 渐变色集合
     */
    fun setProgColor(@ColorRes colorArray: IntArray?) {
        if (colorArray == null || colorArray.size < 2) return
        mColorArray = IntArray(colorArray.size)
        for (index in colorArray.indices) mColorArray!![index] = ContextCompat.getColor(
            context, colorArray[index]
        )
        mProgPaint.shader = LinearGradient(
            0f, 0f, 0f, measuredWidth.toFloat(),
            mColorArray!!, null, Shader.TileMode.MIRROR
        )
        invalidate()
    }
}

