package com.drivona.speed.widght

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.max

class DotBarViewPagerIndicator @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val barHeight = dp2px(14f)          //统一高度
    private val dotWidth = dp2px(14f)           //未选中短条宽度
    private val itemSpacing = dp2px(36f)         //点位间距
    private val minBarWidth = dp2px(28f)        //静止选中条最小宽度
    private val sideExtra = minBarWidth / 2f    //左右预留余量，防止首尾长条裁切

    private val paintUnSelect = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFFFFFFFF.toInt()
        style = Paint.Style.FILL
    }
    private val paintSelect = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFFFF6600.toInt()
        style = Paint.Style.FILL
    }
    private val paintTrackBg = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFF2A2A2A.toInt()
        style = Paint.Style.FILL
    }

    private var _pageCount = 0
    var pageCount: Int
        get() = _pageCount
        set(value) {
            if (_pageCount == value) return
            _pageCount = max(value, 0)
            requestLayout()
            if (position >= _pageCount) {
                position = max(0, _pageCount - 1)
            }
            invalidate()
        }

    private var position = 0
    private var positionOffset = 0f

    fun onPageScrolled(pos: Int, offset: Float) {
        if (_pageCount <= 0) return
        position = pos.coerceIn(0, _pageCount - 1)
        positionOffset = offset.coerceIn(0f, 1f)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (_pageCount <= 0) return

        val centerY = height / 2f
        val dotXCenterList = mutableListOf<Float>()
        var itemCenterX = paddingLeft.toFloat() + sideExtra + dotWidth / 2f
        repeat(_pageCount) {
            dotXCenterList.add(itemCenterX)
            itemCenterX += dotWidth + itemSpacing
        }

        //底层轨道背景
        val trackLeft = dotXCenterList.first() - dotWidth / 2
        val trackRight = dotXCenterList.last() + dotWidth / 2
        canvas.drawRoundRect(
            RectF(trackLeft, centerY - barHeight / 2, trackRight, centerY + barHeight / 2),
            barHeight / 2, barHeight / 2, paintTrackBg
        )

        //绘制未选中白色短条
        for ((index, centerX) in dotXCenterList.withIndex()) {
            val isActive: Boolean
            if (positionOffset <= 0.001f) {
                //静止：只隐藏当前页
                isActive = index == position
            } else {
                //滑动中：隐藏当前 + 下一页；做边界保护，防止position+1越界
                val nextIndex = position + 1
                isActive = (index == position) || (nextIndex < _pageCount && index == nextIndex)
            }

            //非active才画白色条
            if (!isActive) {
                val l = centerX - dotWidth / 2
                val r = centerX + dotWidth / 2
                canvas.drawRoundRect(
                    RectF(l, centerY - barHeight / 2, r, centerY + barHeight / 2),
                    barHeight / 2, barHeight / 2, paintUnSelect
                )
            }
        }

        //绘制橙色选中长条
        val currentCenterX = dotXCenterList[position]
        val nextCenterX = if (position + 1 < _pageCount) dotXCenterList[position + 1] else currentCenterX

        var barLeft: Float
        var barRight: Float
        if (positionOffset <= 0.001f) {
            barLeft = currentCenterX - minBarWidth / 2
            barRight = currentCenterX + minBarWidth / 2
        } else {
            barLeft = currentCenterX
            barRight = currentCenterX + (nextCenterX - currentCenterX) * positionOffset
        }

        if (barRight > barLeft) {
            canvas.drawRoundRect(
                RectF(barLeft, centerY - barHeight / 2, barRight, centerY + barHeight / 2),
                barHeight / 2, barHeight / 2, paintSelect
            )
        }
    }

    private fun dp2px(dp: Float): Float {
        return dp * resources.displayMetrics.density
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (_pageCount <= 0) {
            setMeasuredDimension(0, 0)
            return
        }
        val contentW = (_pageCount * dotWidth + (_pageCount - 1) * itemSpacing).toInt()
        val totalW = (paddingLeft + paddingRight + contentW + sideExtra * 2).toInt()
        val minH = (barHeight * 1.5f).toInt()
        val realH = MeasureSpec.getSize(heightMeasureSpec).coerceAtLeast(minH)
        setMeasuredDimension(totalW, realH)
    }
}
