package com.drivona.speed.utils

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.text.TextPaint
import com.lalifa.extension.dp
import com.lalifa.extension.pk
import com.lalifa.extension.sp
import master.flame.danmaku.danmaku.model.BaseDanmaku
import master.flame.danmaku.danmaku.model.android.AndroidDisplayer.DisplayerConfig
import master.flame.danmaku.danmaku.model.android.BaseCacheStuffer


class MyCacheStuffer(var activity: Activity) : BaseCacheStuffer() {

    /**
     * 文字右边间距
     */
    private var RIGHTMARGE = 0f

    /**
     * 文字和头像间距
     */
    private var LEFTMARGE = 0f

    /**
     * 文字和右边线距离
     */
    private var TEXT_RIGHT_PADDING = 0

    /**
     * 文字大小
     */
    private var TEXT_SIZE = 0f

    /**
     * 头像的大小
     */
    private var IMAGEHEIGHT = 0f

    var textColor: Int = Color.parseColor("#ffffff")

    init {
        // 初始化固定参数，这些参数可以根据自己需求自行设定
        LEFTMARGE = 6.dp.toFloat()
        RIGHTMARGE = 12.dp.toFloat()
        IMAGEHEIGHT = 23.dp.toFloat()
        TEXT_SIZE = 8.sp * (activity.resources.displayMetrics.density - 0.6f)
    }

    override fun measure(danmaku: BaseDanmaku?, paint: TextPaint?, fromWorkerThread: Boolean) {
        //测量的相应方法
        // 初始化数据
        val map = danmaku!!.tag as Map<String, Any>
        val content = map["content"] as String?
        val bitmap = map["bitmap"] as Bitmap?

        // 设置画笔
        paint!!.textSize = TEXT_SIZE

        // 计算名字和内容的长度，取最大值
        val contentWidth = paint!!.measureText(content)

        // 设置弹幕区域的宽度
        danmaku!!.paintWidth = contentWidth + IMAGEHEIGHT + LEFTMARGE + RIGHTMARGE
        // 设置弹幕区域的高度
        danmaku!!.paintHeight = IMAGEHEIGHT * 2
    }

    override fun clearCaches() {
        //用来释放或者清除一些资源
    }

    override fun drawDanmaku(danmaku: BaseDanmaku?, canvas: Canvas?, left: Float, top: Float, fromWorkerThread: Boolean, displayerConfig: DisplayerConfig?) {
        //绘制的相应方法
        // 初始化数据
        val map = danmaku!!.tag as Map<String, Any>
        val content = map["content"] as String?
        val bitmap = map["bitmap"] as Bitmap?
        val color = map["color"] as String?
        val text_color = map["text_color"] as String?

        if (isColor(text_color.pk())) Color.parseColor(text_color.pk()) else Color.WHITE

        // 设置画笔
        val paint = Paint()
        paint.textSize = TEXT_SIZE

        //绘制背景
        val textLength = paint.measureText(content).toInt()
        //随机数，主要是为了生成不同颜色的背景的
        paint.color = Color.parseColor(color)

        //获取图片的宽度
        val rectBgLeft = left
        val rectBgTop = top
        val rectBgRight = left + IMAGEHEIGHT + textLength + LEFTMARGE + RIGHTMARGE
        val rectBgBottom = top + IMAGEHEIGHT
        canvas!!.drawRoundRect(RectF(rectBgLeft, rectBgTop, rectBgRight, rectBgBottom), IMAGEHEIGHT / 2, IMAGEHEIGHT / 2, paint)

        // 绘制头像
        val avatorRight = left + IMAGEHEIGHT
        val avatorLeft = left + IMAGEHEIGHT
        val avatorTop = left + IMAGEHEIGHT
        val avatorBottom = top + IMAGEHEIGHT
        paint.color = Color.WHITE
        bitmap?.let {
            canvas!!.drawBitmap(it, null, RectF(left, top, avatorRight, avatorBottom), paint)
        }

        // 绘制弹幕内容,文字白色的
        paint.color = textColor
        val contentLeft = left + IMAGEHEIGHT + LEFTMARGE
        //计算文字的相应偏移量
        val fontMetrics = paint.fontMetrics
        //为基线到字体上边框的距离,即上图中的top
        val textTop = fontMetrics.top
        //为基线到字体下边框的距离,即上图中的bottom
        val textBottom = fontMetrics.bottom

        val contentBottom = top + IMAGEHEIGHT / 2
        //基线中间点的y轴计算公式
        val baseLineY = (contentBottom - textTop / 2 - textBottom / 2).toInt()
        //绘制文字
        canvas!!.drawText(content!!, contentLeft, baseLineY.toFloat(), paint)
    }

    fun isColor(str: String): Boolean {
        val pattern = Regex("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})\$")
        return pattern.matches(str)
    }

}