package com.erkaslan.storybox.ui.component

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.LinearLayout
import android.widget.ProgressBar
import com.erkaslan.storybox.R
import com.erkaslan.storybox.data.models.StoryGroup
import com.erkaslan.storybox.data.models.StoryType
import com.erkaslan.storybox.ui.adapter.StoryDetailListener

class StoryGroupProgressView : LinearLayout {

    companion object {
        const val SPACE_BETWEEN_BARS = 5
        const val PROGRESS_LIMIT = 1000
        const val DEFAULT_DURATION = 5000L
    }

    private var totalBarCount: Int? = null
    private var currentAnimator: Animator? = null
    private var progressBarList = mutableListOf<ProgressBar>()

    private var listener: StoryDetailListener? = null
    private var position: Int = 0
    var currentStoryIndex: Int = -1
    private var storyGroup: StoryGroup? = null
    var onPauseVideo: (() -> Unit)? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    )

    fun initializeProgressView (storyGroup: StoryGroup, adapterPosition: Int, touchListener: StoryDetailListener?, duration: Long? = null) {
        if (storyGroup.storyList[storyGroup.lastStoryIndex].type == StoryType.IMAGE && storyGroup.lastStoryIndex == currentStoryIndex) return
        reset()
        this.storyGroup = storyGroup
        this.listener = touchListener
        this.totalBarCount = storyGroup.storyList.size
        this.position = adapterPosition
        this.currentStoryIndex = storyGroup.lastStoryIndex

        repeat(storyGroup.storyList.size) { index ->
            if (index >= currentStoryIndex) addProgressBar(0)
            else addProgressBar(PROGRESS_LIMIT)

            if (index + 1 != totalBarCount) addSpace()
        }
        setAnimator(duration = duration ?: DEFAULT_DURATION)
    }

    fun setAnimator(itemNumber: Int = currentStoryIndex, duration: Long = DEFAULT_DURATION) {
        if (progressBarList.isNotEmpty()) {
            currentAnimator = null
            currentAnimator =
                ObjectAnimator.ofInt(progressBarList[itemNumber], "progress", PROGRESS_LIMIT)
                    .also { animation ->
                        animation.duration = duration
                        animation.interpolator = LinearInterpolator()
                        animation.addListener(object : AnimatorListener {
                            override fun onAnimationStart(p0: Animator?) {}
                            override fun onAnimationCancel(p0: Animator?) {
                                p0?.removeAllListeners()
                            }

                            override fun onAnimationRepeat(p0: Animator?) {}
                            override fun onAnimationEnd(p0: Animator?) {
                                Log.d(
                                    "STORYBOX",
                                    "progress end: " + storyGroup?.username + " " + " " + currentStoryIndex + " duration: " + duration
                                )
                                onPauseVideo?.invoke()
                                listener?.onStoryNextClicked(storyGroup, position)
                            }
                        })
                    }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun addProgressBar(progress: Int) {
        val view = ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal)
        view.layoutParams = LayoutParams(0, (2 * context.resources.displayMetrics.density).toInt(), 1f)
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

    private fun addSpace() {
        val spaceView = View(context).also { it.layoutParams = LayoutParams(SPACE_BETWEEN_BARS, android.view.ViewGroup.LayoutParams.WRAP_CONTENT) }
        addView(spaceView)
    }

    fun resumeProgress() {
        if (currentAnimator?.isPaused == true) currentAnimator?.resume()
        else currentAnimator?.start()
    }

    fun pauseProgress() { currentAnimator?.pause() }

    fun resetAll() {
        currentAnimator?.removeAllListeners()
        reset()
    }

    private fun reset() {
        removeAllViews()
        progressBarList.clear()
        currentAnimator = null
    }

    fun restart () {
        if (progressBarList.isNotEmpty()) { progressBarList[position].progress = 0 }
    }
}