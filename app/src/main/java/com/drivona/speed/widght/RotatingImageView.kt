package com.drivona.speed.widght

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PixelFormat
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.util.AttributeSet
import android.view.animation.LinearInterpolator
import androidx.appcompat.widget.AppCompatImageView

class RotatingImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {
    private var isRotating = false
    private var rotationDirection = 1
    private var mSpeed = 2000L
    private var rotationAnimator: ObjectAnimator? = null
    private val path = Path()
    private val paint = Paint().apply {
        isAntiAlias = true
        isDither = true // 添加抗锯齿标志位
        xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    }

    fun setRotationDirection(direction: Int) {
        rotationDirection = direction
        updateRotationAnimator()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startRotation()
    }

    override fun onDetachedFromWindow() {
        stopRotation()
        super.onDetachedFromWindow()
    }

    override fun onDraw(canvas: Canvas) {
        val drawable = drawable ?: return

        if (width == 0 || height == 0) {
            return
        }

        val bitmap = (drawable as BitmapDrawable).bitmap
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false)

        path.reset()
        path.addCircle(
            width / 2f,
            height / 2f,
            width.coerceAtMost(height) / 2f,
            Path.Direction.CW
        )

        val saveCount = canvas.saveLayer(0f, 0f, width.toFloat(), height.toFloat(), null)
        canvas.drawCircle(
            width / 2f,
            height / 2f,
            width.coerceAtMost(height) / 2f,
            paint
        )
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(scaledBitmap, 0f, 0f, paint)
        paint.xfermode = null

        val gradientRadius = width.coerceAtMost(height) / 2f
        val gradientPaint = Paint().apply {
            shader = RadialGradient(
                width / 2f,
                height / 2f,
                gradientRadius,
                Color.TRANSPARENT,
                Color.TRANSPARENT,
                Shader.TileMode.CLAMP
            )
            xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
        }

        canvas.drawCircle(
            width / 2f,
            height / 2f,
            gradientRadius,
            gradientPaint
        )

        // 绘制向下的大弧度效果
//        drawDownArc(canvas, width.toFloat(), height.toFloat(), width.coerceAtMost(height) / 2f)
//        applyBlurEffect(canvas, bitmap)
        canvas.restoreToCount(saveCount)
    }

    private fun drawDownArc(canvas: Canvas, width: Float, height: Float, radius: Float) {
        val arcRect = RectF(0f, height - radius * 2, width, height)
        val arcPaint = Paint().apply {
            isAntiAlias = true
            color = Color.BLACK
            style = Paint.Style.FILL
        }

        val path = Path()
        path.arcTo(arcRect, 180f, 180f)
        path.lineTo(width, height)
        path.lineTo(0f, height)
        path.close()

        canvas.drawPath(path, arcPaint)
    }

    private fun applyBlurEffect(canvas: Canvas, bitmap: Bitmap) {
        val blurredBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)

        val renderScript = RenderScript.create(context)
        val blurScript = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))
        val allocationIn = Allocation.createFromBitmap(renderScript, bitmap)
        val allocationOut = Allocation.createFromBitmap(renderScript, blurredBitmap)

        blurScript.setRadius(25f) // 模糊半径可以根据需要进行调整
        blurScript.setInput(allocationIn)
        blurScript.forEach(allocationOut)

        allocationOut.copyTo(blurredBitmap)
        renderScript.destroy()

        canvas.drawBitmap(blurredBitmap, 0f, 0f, null)
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
    }

    fun getOpacity(): Int {
        return PixelFormat.TRANSPARENT
    }

    fun setRotationSpeed(speed: Long) {
        mSpeed = speed
        startRotation()
    }

    private fun startRotation() {
        updateRotationAnimator()

        rotationAnimator?.apply {
            rotationAnimator?.duration = mSpeed
            repeatMode = ObjectAnimator.RESTART
            repeatCount = ObjectAnimator.INFINITE
            start()
        }
    }

    public fun stopRotation() {
        rotationAnimator?.cancel()
    }

    private fun updateRotationAnimator() {
        rotationAnimator?.cancel()

        val rotationProperty = if (rotationDirection == -1) "rotation" else "rotationY"
        rotationAnimator = ObjectAnimator.ofFloat(this, rotationProperty, 0f, 360f)
        rotationAnimator?.setInterpolator(LinearInterpolator());
    }
}
