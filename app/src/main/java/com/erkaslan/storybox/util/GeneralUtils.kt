package com.erkaslan.storybox.util

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.res.Resources
import android.view.View
import android.view.animation.LinearInterpolator

object GeneralUtils {
    private const val STORY_SLIDE_ANIMATION_DURATION = 500L

    fun dpToPx(dp: Int): Int = (dp * Resources.getSystem().displayMetrics.density).toInt()

    fun slideStory(view: View, fromValue: Int, toValue: Int) {
        view.translationY = fromValue.toFloat()

        val valueAnimator = ValueAnimator.ofInt(dpToPx(fromValue), dpToPx(toValue)).setDuration(STORY_SLIDE_ANIMATION_DURATION)

        valueAnimator.interpolator = LinearInterpolator()

        valueAnimator.addUpdateListener {
            val value = it.animatedValue as Int
            view.translationY = value.toFloat()
        }

        val animatorSet = AnimatorSet()
        animatorSet.addListener(object: Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator?) {}
            override fun onAnimationRepeat(p0: Animator?) {}
            override fun onAnimationEnd(p0: Animator?) {
                view.visibility = View.GONE
                view.translationY = fromValue.toFloat()
            }
            override fun onAnimationCancel(p0: Animator?) {
                view.visibility = View.GONE
                view.translationY = fromValue.toFloat()
            }
        })
        animatorSet.play(valueAnimator)
        animatorSet.start()
    }
}