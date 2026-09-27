package com.drivona.speed.widght
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class PageBarIndicator @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val dotRadius = dp2px(16f)
    private val trackHeight = dp2px(12f)
    private val dotSpacing = dp2px(48f)

    private val paintDot = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFFFFFFFF.toInt()
    }
    private val paintActiveBar = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFFFF7700.toInt()
    }
    private val paintTrackBg = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFF333333.toInt()
    }

    private var _pageCount = 0
    var pageCount: Int
        get() = _pageCount
        set(value) {
            if (_pageCount == value) return
            //边界保护，至少0页
            _pageCount = maxOf(value,0)
            //页数改变，需要重新测量宽高，requestLayout，不能只invalidate
            requestLayout()
            //保护当前position，防止越界
            if(position >= _pageCount){
                position = maxOf(0,_pageCount -1)
            }
            invalidate()
        }

    private var position: Int = 0
    private var positionOffset: Float = 0f

    fun setPageState(pos: Int, offset: Float) {
        if(_pageCount <=0) return
        position = pos.coerceIn(0,_pageCount-1)
        positionOffset = offset.coerceIn(0f,1f)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if(_pageCount <=0) return

        val centerY = height / 2f
        var startX = paddingLeft + dotRadius

        val dotXList = mutableListOf<Float>()
        for (i in 0 until _pageCount) {
            dotXList.add(startX + i * (dotRadius *2 + dotSpacing))
        }

        val trackLeft = dotXList.first() - dotRadius
        val trackRight = dotXList.last() + dotRadius
        canvas.drawRoundRect(
            RectF(trackLeft, centerY-trackHeight/2, trackRight, centerY+trackHeight/2),
            trackHeight/2, trackHeight/2, paintTrackBg
        )

        val leftDotX = dotXList[position]
        val rightDotX = if(position+1 < _pageCount) dotXList[position+1] else dotXList[position]
        val barEndX = leftDotX + (rightDotX - leftDotX)*positionOffset

        canvas.drawRoundRect(
            RectF(leftDotX, centerY-trackHeight/2, barEndX, centerY+trackHeight/2),
            trackHeight/2, trackHeight/2, paintActiveBar
        )

        for(x in dotXList){
            canvas.drawCircle(x, centerY, dotRadius, paintDot)
        }
    }

    private fun dp2px(dp:Float):Float{
        return dp * resources.displayMetrics.density
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if(_pageCount <= 0){
            setMeasuredDimension(0,0)
            return
        }
        val contentWidth = (_pageCount*(dotRadius*2) + (_pageCount-1)*dotSpacing).toInt()
        val totalWidth = paddingLeft + paddingRight + contentWidth
        val minHeight = (dotRadius*2).toInt()
        val height = MeasureSpec.getSize(heightMeasureSpec).coerceAtLeast(minHeight)
        setMeasuredDimension(totalWidth, height)
    }
}
