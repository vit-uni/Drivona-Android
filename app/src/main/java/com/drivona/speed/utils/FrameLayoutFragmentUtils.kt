package com.drivona.speed.utils

import android.animation.Animator
import android.animation.AnimatorInflater
import android.content.Context
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.drivona.speed.R

class FrameLayoutFragmentUtils(
    private val context: Context,
    private val frameLayoutId: Int,
    private val fragmentManager: FragmentManager
) {
    private var currentFragment: Fragment? = null

    fun addFragment(fragment: Fragment) {
        fragmentManager.beginTransaction()
            .add(frameLayoutId, fragment)
            .hide(fragment)
            .commit()
        currentFragment = fragment
    }

    fun removeFragment(fragment: Fragment) {
        fragmentManager.beginTransaction()
            .remove(fragment)
            .commit()
        currentFragment = null
    }

    fun showFragment(fragment: Fragment, withAnimation: Boolean = true, callBack: () -> Unit = {}) {
        val transaction = fragmentManager.beginTransaction()
        if (withAnimation) {
            val slideInAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_in)
            fragment.view?.startAnimation(slideInAnimation)
        }
        transaction.show(fragment)
        currentFragment?.let {
            if (withAnimation) {
                val slideOutAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_out)
                it.view?.startAnimation(slideOutAnimation)
                slideOutAnimation.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {

                    }

                    override fun onAnimationEnd(animation: Animation?) {
                        callBack.invoke()
                    }

                    override fun onAnimationRepeat(animation: Animation?) {

                    }

                })
            }
            transaction.hide(it)
        }
        transaction.commit()
        currentFragment = fragment
    }

    fun hideFragment(withAnimation: Boolean = true) {
        currentFragment?.let { fragment ->
            val transaction = fragmentManager.beginTransaction()
            if (withAnimation) {
                val slideOutAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_out)
                fragment.view?.startAnimation(slideOutAnimation)
                slideOutAnimation.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {}
                    override fun onAnimationRepeat(animation: Animation?) {}
                    override fun onAnimationEnd(animation: Animation?) {
                        transaction.hide(fragment).commit()
                        currentFragment = null
                    }
                })
            } else {
                transaction.hide(fragment).commit()
                currentFragment = null
            }
        }
    }

    fun showFragmentWithAnimator(fragment: Fragment, animatorResId: Int) {
        val transaction = fragmentManager.beginTransaction()
        val animator = AnimatorInflater.loadAnimator(context, animatorResId)
        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                transaction.show(fragment).commit()
                currentFragment = fragment
            }
        })
        animator.setTarget(fragment.view)
        transaction.hide(currentFragment ?: fragment)
        animator.start()
    }

    fun hideFragmentWithAnimator(animatorResId: Int) {
        currentFragment?.let { fragment ->
            val transaction = fragmentManager.beginTransaction()
            val animator = AnimatorInflater.loadAnimator(context, animatorResId)
            animator.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
                override fun onAnimationEnd(animation: Animator) {
                    transaction.hide(fragment).commit()
                    currentFragment = null
                }
            })
            animator.setTarget(fragment.view)
            animator.start()
        }
    }
}
