package com.erkaslan.storybox.ui.component

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.LinearLayout
import android.widget.ProgressBar
import com.erkaslan.storybox.R

class StoryGroupProgressView : LinearLayout {

    companion object {
        const val SPACE = 5
        const val PROGRESS_LIMIT = 1000
        const val DEFAULT_DURATION = 5000L
    }

    private var totalBarCount: Int? = null
    private var currentStoryNumber: Int = 1
    private var currentTime: Int = 0
    private var currentAnimator: Animator? = null
    private var progressBarList = mutableListOf<ProgressBar>()
    var progressListener: AnimatorListener? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    )

    fun initializeProgressView (totalBarCount: Int, currentStoryNumber: Int, progressListener: AnimatorListener) {
        reset()
        this.progressListener = progressListener
        this.totalBarCount = totalBarCount
        this.currentStoryNumber = currentStoryNumber

        repeat(totalBarCount) { index ->
            if (index + 1 > currentStoryNumber) addProgressBar(0)
            else if (index + 1 == currentStoryNumber) addProgressBar(currentTime)
            else addProgressBar(PROGRESS_LIMIT)

            if (index + 1 != totalBarCount) addSpace()
        }
        setAnimator()
    }

    fun setAnimator(itemNumber: Int = currentStoryNumber - 1) {
        currentAnimator = ObjectAnimator.ofInt(progressBarList[itemNumber], "progress", PROGRESS_LIMIT).also { animation ->
            animation.duration = DEFAULT_DURATION
            animation.interpolator = LinearInterpolator()
            progressListener?.let { animation.addListener(it) }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun addProgressBar(progress: Int) {
        val view = ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal)
        view.layoutParams = LayoutParams(0, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        view.isIndeterminate = false
        view.progressDrawable = context.getDrawable(R.drawable.progress_story)
        view.max = PROGRESS_LIMIT
        view.progress = progress
        addBar(view)
    }

    private fun addBar(view: ProgressBar) {
        addView(view)
        progressBarList.add(view)
    }

    fun reset() {
        removeAllViews()
        progressBarList.clear()
        currentAnimator = null
        progressListener = null
        currentTime = 0
    }

    private fun addSpace() {
        val spaceView = View(context).also { it.layoutParams = LayoutParams(SPACE, android.view.ViewGroup.LayoutParams.WRAP_CONTENT) }
        addView(spaceView)
    }

    fun startAnimation() { currentAnimator?.start() }

    fun pauseAnimation() { currentAnimator?.pause() }
}