package com.drivona.speed.utils

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatImageView

/**
 *单击可放大的 ImageView 视图类
 */
class ClickToZoomImageView : AppCompatImageView, View.OnClickListener {

    private var isZoomedIn = false
    private var defaultScale = 1.0f
    private var zoomedInScale = 1.4f

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    init {
        // 设置单击事件监听器
        this.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        // 使用 ObjectAnimator 使 ImageView 变形
        val scaleX = ObjectAnimator.ofFloat(
            this,
            View.SCALE_X, if (isZoomedIn) zoomedInScale else defaultScale,
            if (isZoomedIn) defaultScale else zoomedInScale
        )
        val scaleY = ObjectAnimator.ofFloat(
            this,
            View.SCALE_Y, if (isZoomedIn) zoomedInScale else defaultScale,
            if (isZoomedIn) defaultScale else zoomedInScale
        )

        // 使用 AnimatorSet 同时播放 X 和 Y 的动画，设置一个动画持续时间为 200ms
        val animatorSet = AnimatorSet()
        animatorSet.duration = 200
        animatorSet.playTogether(scaleX, scaleY)
        animatorSet.start()

        // 修改变量，在下次单击时触发不同的动画
        isZoomedIn = !isZoomedIn

        // 如果放大视图，则将其置于视图的中心
        if (isZoomedIn) {
            this.pivotX = this.width / 2f
            this.pivotY = this.height / 2f
        }
    }
}