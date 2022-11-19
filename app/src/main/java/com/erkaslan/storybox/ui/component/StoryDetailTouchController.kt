package com.erkaslan.storybox.ui.component

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.constraintlayout.widget.ConstraintLayout
import com.erkaslan.storybox.data.models.StoryGroup
import kotlin.math.abs

class StoryDetailTouchController(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {

    companion object {
        const val SWIPE_THRESHOLD = 150
        const val STORY_TRANSITION_DIRECTION_THRESHOLD = 0.3
    }

    private var touchInitialTime = 0L
    var touchFinalTime = 0L
    var touchInitialPoint = Point(0F,0F)
    var touchFinalPoint = Point(0F,0F)
    var moveDirection: Direction = Direction.UP
    private var listener: StoryTouchListener? = null
    private var position: Int? = null
    private var storyGroup: StoryGroup? = null

    fun setController(position: Int, storyGroup: StoryGroup, storyTouchListener: StoryTouchListener?) {
        this.position = position
        this.storyGroup = storyGroup
        this.listener = storyTouchListener
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                touchInitialTime = System.currentTimeMillis()
                touchInitialPoint = Point(event.x, event.y)
                listener?.onPause()
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
                    listener?.onClose()
                    return true
                }

                if (duration < SWIPE_THRESHOLD) {
                    if((touchFinalPoint.x + touchInitialPoint.x)/2 > this@StoryDetailTouchController.width * STORY_TRANSITION_DIRECTION_THRESHOLD) {
                        listener?.onNextClicked()
                    } else {
                        listener?.onPreviousClicked()
                    }
                } else {
                    if (moveDirection == Direction.LEFT) {
                        listener?.onPreviousClicked()
                    } else if (moveDirection == Direction.RIGHT) {
                        listener?.onNextClicked()
                    }
                }
                listener?.onResume()
                return true
            }
            else -> return true
        }
    }

    override fun callOnClick(): Boolean {
        listener?.onNextClicked()
        return true
    }
}

enum class Direction { UP, DOWN, LEFT, RIGHT }
class Point(val x: Float, val y: Float)

interface StoryTouchListener {
    fun onNextClicked()
    fun onPreviousClicked()
    fun onResume()
    fun onPause()
    fun onClose()
}