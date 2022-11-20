package com.erkaslan.storybox.ui.component

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.LinearLayout
import android.widget.ProgressBar
import com.erkaslan.storybox.R
import com.erkaslan.storybox.data.models.StoryGroup
import com.erkaslan.storybox.data.models.StoryType
import com.erkaslan.storybox.ui.adapter.StoryDetailListener
import kotlin.math.abs

class StoryGroupProgressView : LinearLayout {

    companion object {
        const val SPACE_BETWEEN_BARS = 5
        const val PROGRESS_LIMIT = 1000
        const val DEFAULT_DURATION = 5000L

        const val SWIPE_THRESHOLD = 150
        const val STORY_TRANSITION_DIRECTION_THRESHOLD = 0.3
    }

    private var totalBarCount: Int? = null
    private var currentTime: Int = 0
    private var currentAnimator: Animator? = null
    private var progressBarList = mutableListOf<ProgressBar>()

    private var touchInitialTime = 0L
    var touchFinalTime = 0L
    var touchInitialPoint = Point(0F,0F)
    var touchFinalPoint = Point(0F,0F)
    var moveDirection: Direction = Direction.UP
    private var listener: StoryDetailListener? = null
    private var position: Int = 0
    private var currentStoryIndex: Int = 0
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
        reset()
        this.storyGroup = storyGroup
        this.listener = touchListener
        this.totalBarCount = storyGroup.storyList.size
        this.position = adapterPosition
        this.currentStoryIndex = storyGroup.lastStoryIndex

        repeat(storyGroup.storyList.size) { index ->
            if (index > currentStoryIndex) addProgressBar(0)
            else if (index == currentStoryIndex) addProgressBar(currentTime)
            else addProgressBar(PROGRESS_LIMIT)

            if (index + 1 != totalBarCount) addSpace()
        }
        setAnimator(duration = duration ?: DEFAULT_DURATION)
    }

    fun setAnimator(itemNumber: Int = currentStoryIndex, duration: Long = DEFAULT_DURATION) {
        currentAnimator = null
        currentAnimator = ObjectAnimator.ofInt(progressBarList[itemNumber], "progress", PROGRESS_LIMIT).also { animation ->
            animation.duration = duration
            animation.interpolator = LinearInterpolator()
            animation.addListener(object : AnimatorListener {
                override fun onAnimationStart(p0: Animator?) { }

                override fun onAnimationEnd(p0: Animator?) {
                    Log.d("TEST", "progress end")
                    onPauseVideo?.invoke()
                    listener?.onStoryNextClicked(storyGroup, position)
                }

                override fun onAnimationCancel(p0: Animator?) { p0?.removeAllListeners() }

                override fun onAnimationRepeat(p0: Animator?) { }
            })
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

    private fun resetAll() {
        currentAnimator?.removeAllListeners()
        reset()
    }

    private fun reset() {
        removeAllViews()
        progressBarList.clear()
        currentAnimator = null
        currentTime = 0
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                //Log.d("TEST", "down")
                touchInitialTime = System.currentTimeMillis()
                touchInitialPoint = Point(event.x, event.y)
                currentAnimator?.pause()
                if (storyGroup?.storyList?.get(currentStoryIndex)?.type == StoryType.VIDEO)
                    onPauseVideo?.invoke()
                return true
            }
            MotionEvent.ACTION_UP -> {
                //Log.d("TEST", "up")
                touchFinalTime = System.currentTimeMillis()
                val duration = touchFinalTime - touchInitialTime
                touchFinalPoint = Point(event.x, event.y)

                val deltaX = touchFinalPoint.x - touchInitialPoint.x
                val deltaY = touchFinalPoint.y - touchInitialPoint.y
                moveDirection =
                    if (abs(deltaY) * 0.5 < abs(deltaX)) {
                        if (deltaX > 0) Direction.RIGHT else Direction.LEFT
                    } else {
                        if(deltaY > 0) Direction.DOWN else Direction.UP
                    }

                if (moveDirection == Direction.DOWN) {
                    //Log.d("TEST", "swipe bottom")
                    resetAll()
                    listener?.onCloseStory(storyGroup, position)
                    return true
                }

                if (duration < SWIPE_THRESHOLD) {
                    //Log.d("TEST", "tap")
                    resetAll()
                    if ((touchFinalPoint.x + touchInitialPoint.x)/2 > this@StoryGroupProgressView.width * STORY_TRANSITION_DIRECTION_THRESHOLD) {
                        listener?.onStoryNextClicked(storyGroup, position)
                        return true
                    } else {
                        listener?.onStoryPreviousClicked(storyGroup, position)
                        return true
                    }
                } else {
                    //Log.d("TEST", "2")
                    resetAll()
                    if (moveDirection == Direction.LEFT) {
                        listener?.onStoryPreviousClicked(storyGroup, position)
                        return true
                    } else if (moveDirection == Direction.RIGHT) {
                        listener?.onStoryNextClicked(storyGroup, position)
                        return true
                    } else {
                        listener?.onResumeVideo(storyGroup, position)
                    }
                }
                return true
            }
            else -> return true
        }
    }
}

enum class Direction { UP, DOWN, LEFT, RIGHT }
class Point(val x: Float, val y: Float)