package com.hjq.widget.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.annotation.Nullable

class TestView @JvmOverloads constructor(
    context: Context,
    @Nullable attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        //新建一只画笔，并设置为绿色属性
        var _paint = Paint();
        _paint.setAntiAlias(true) //设置画笔为无锯齿

        _paint.setColor(Color.BLACK) //设置画笔颜色

        canvas.drawColor(Color.WHITE) //白色背景
        _paint.style = Paint.Style.STROKE // 只描边，不填充
        _paint.strokeCap = Paint.Cap.ROUND // 设置圆角
        _paint.isAntiAlias = true // 设置抗锯齿
        _paint.isDither = true // 设置抖动
        _paint.setStrokeWidth(3.0.toFloat()) //线宽

        _paint.setStyle(Paint.Style.STROKE) //空心效果

        //新建矩形r1
        var r1 = RectF();
        r1.left = 50f;
        r1.right = 250f;
        r1.top = 50f ;
        r1.bottom = 150f;

        //新建矩形r2
        var r2 = RectF();
        r2.left = 50f;
        r2.right = 450f;
        r2.top = 400f ;
        r2.bottom = 650f;

        //画出矩形r1
        canvas.drawRect(r1, _paint);
        //画出圆角矩形r2
        _paint.setColor(Color.rgb(204, 204, 204));
        canvas.drawRoundRect(r2, 45f, 45f, _paint);
    }
}