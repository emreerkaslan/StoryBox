package com.erkaslan.storybox.ui.component

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.LinearLayout
import android.widget.ProgressBar
import com.erkaslan.storybox.R
import com.erkaslan.storybox.data.models.StoryGroup
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

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    )

    fun initializeProgressView (storyGroup: StoryGroup, adapterPosition: Int, touchListener: StoryDetailListener?) {
        resetAll()
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
        setAnimator()
    }

    private fun setAnimator(itemNumber: Int = currentStoryIndex) {
        currentAnimator = ObjectAnimator.ofInt(progressBarList[itemNumber], "progress", PROGRESS_LIMIT).also { animation ->
            animation.duration = DEFAULT_DURATION
            animation.interpolator = LinearInterpolator()
            animation.addListener(object : AnimatorListener {
                override fun onAnimationStart(p0: Animator?) { }

                override fun onAnimationEnd(p0: Animator?) {
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

    fun startAnimation() { currentAnimator?.start() }

    fun pauseAnimation() { currentAnimator?.pause() }

    fun reset() {
        currentAnimator?.removeAllListeners()
        resetAll()
    }

    fun resetAll() {
        removeAllViews()
        progressBarList.clear()
        currentAnimator = null
        currentTime = 0
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                touchInitialTime = System.currentTimeMillis()
                touchInitialPoint = Point(event.x, event.y)
                currentAnimator?.pause()
                listener?.onPauseVideo(storyGroup, position)
                return true
            }
            MotionEvent.ACTION_UP -> {
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
                    reset()
                    listener?.onCloseStory(storyGroup, position)
                    return true
                }

                if (duration < SWIPE_THRESHOLD) {
                    if((touchFinalPoint.x + touchInitialPoint.x)/2 > this@StoryGroupProgressView.width * STORY_TRANSITION_DIRECTION_THRESHOLD) {
                        reset()
                        listener?.onStoryNextClicked(storyGroup, position)
                    } else {
                        reset()
                        listener?.onStoryPreviousClicked(storyGroup, position)
                    }
                } else {
                    if (moveDirection == Direction.LEFT) {
                        reset()
                        listener?.onStoryPreviousClicked(storyGroup, position)
                    } else if (moveDirection == Direction.RIGHT) {
                        reset()
                        listener?.onStoryNextClicked(storyGroup, position)
                    }
                }
                listener?.onResumeVideo(storyGroup, position)
                return true
            }
            else -> return true
        }
    }
}

enum class Direction { UP, DOWN, LEFT, RIGHT }
class Point(val x: Float, val y: Float)