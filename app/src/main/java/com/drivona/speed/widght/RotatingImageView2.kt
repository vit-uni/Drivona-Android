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
import android.graphics.RectF
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.util.AttributeSet
import android.view.animation.LinearInterpolator
import com.google.android.material.imageview.ShapeableImageView

class RotatingImageView2 @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ShapeableImageView(context, attrs, defStyleAttr) {
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
