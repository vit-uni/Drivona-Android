package com.drivona.speed.widght

import android.animation.AnimatorInflater
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import com.drivona.speed.R


class MoveImageView(context: Context, attrs: AttributeSet) : ImageView(context, attrs){

    var valueAnimator:ValueAnimator? = null

    init {
//        startAnimator()
    }

    fun startAnimator() {
        move(this)
    }

    fun startAnimator(view: View) {
        move(view)
    }

    @SuppressLint("WrongConstant", "ResourceType")
    private fun move(view: View) {
        val animator = AnimatorInflater.loadAnimator(
            context,
            R.anim.translate_animation
        )
        animator.setTarget(this)
        animator.start()
//        view.bringToFront()
//        val width: Int = view.width
//        valueAnimator = ValueAnimator.ofFloat(-width.toFloat(), width.toFloat())
//        valueAnimator?.addUpdateListener { animation: ValueAnimator ->
//            val aFloat = valueAnimator?.animatedValue as Float
//            this@MoveImageView.translationX = aFloat
//            val alpha = aFloat / width
//            val a1 = if (alpha > 0) 1 - alpha else 1 + alpha
//            val a2 = (a1 / 2 + 0.3).toFloat()
////            this@MoveImageView.alpha = a2
//            Log.e("dch", "onFocusChange: ")
//        }
//        valueAnimator?.addListener(object : Animator.AnimatorListener {
//            override fun onAnimationStart(animation: Animator) {
//                visibility = View.VISIBLE
//            }
//
//            override fun onAnimationEnd(animation: Animator) {
//                visibility = View.GONE
//            }
//
//            override fun onAnimationCancel(animation: Animator) {}
//            override fun onAnimationRepeat(animation: Animator) {}
//        })
//        valueAnimator?.interpolator = AccelerateDecelerateInterpolator()
//        val d = width / 355 - 1
//        val ff = 1000 * (d * 0.25f + 1)
//        valueAnimator?.repeatCount = INFINITE;//无限循环
//        valueAnimator?.repeatMode = INFINITE;//
//        valueAnimator?.duration = 3000
//        valueAnimator?.startDelay = 2000
//        valueAnimator?.start()

//        val animators: MutableList<Animator> = ArrayList()
//        val translationXAnim = ObjectAnimator.ofFloat(view, "translationX", -6.0f, 6.0f, -6.0f)
//        translationXAnim.duration = 1500
//        translationXAnim.repeatCount = INFINITE //无限循环
//
//        translationXAnim.repeatMode = INFINITE //
//
//        translationXAnim.start()
//        animators.add(translationXAnim)
//        val translationYAnim = ObjectAnimator.ofFloat(view, "translationY", -3.0f, 3.0f, -3.0f)
//        translationYAnim.duration = 1000
//        translationYAnim.repeatCount = INFINITE
//        translationYAnim.repeatMode = INFINITE
//        translationYAnim.start()
//        animators.add(translationYAnim)
//
//        val btnSexAnimatorSet = AnimatorSet()
//        btnSexAnimatorSet.playTogether(animators)
//        btnSexAnimatorSet.startDelay = delay.toLong()
//        btnSexAnimatorSet.start()
    }

    private fun remove() {
        valueAnimator?.cancel()
    }
}