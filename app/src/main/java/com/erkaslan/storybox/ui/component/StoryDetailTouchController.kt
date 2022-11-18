package com.erkaslan.storybox.ui.component

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import androidx.constraintlayout.widget.ConstraintLayout
import com.erkaslan.storybox.data.models.StoryGroup
import com.erkaslan.storybox.ui.adapter.StoryDetailListener
import kotlin.math.abs

class StoryDetailTouchController(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {

    companion object {
        const val SWIPE_THRESHOLD = 150
    }

    private var touchInitialTime = 0L
    var touchFinalTime = 0L
    var touchInitialPoint = Point(0F,0F)
    var touchFinalPoint = Point(0F,0F)
    var moveDirection: Direction = Direction.UP
    private var listener: StoryDetailListener? = null
    private var position: Int? = null
    private var storyGroup: StoryGroup? = null

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                touchInitialTime = System.currentTimeMillis()
                touchInitialPoint = Point(event.x, event.y)
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
                    if (abs(deltaX) > abs(deltaY)) {
                        if (deltaX > 0) Direction.RIGHT else Direction.LEFT
                    } else {
                        if(deltaY > 0) Direction.DOWN else Direction.UP
                    }

                if (moveDirection == Direction.DOWN) {
                    listener?.onCloseStory()
                    return true
                }

                Log.d("TEST", duration.toString())
                if (duration < SWIPE_THRESHOLD) {
                    if((touchFinalPoint.x + touchInitialPoint.x)/2 > this@StoryDetailTouchController.width/2) {
                        listener?.onStoryNextClicked(storyGroup, position)
                    } else {
                        listener?.onStoryPreviousClicked(storyGroup, position)
                    }
                } else {
                    Log.d("TEST", moveDirection.toString())
                    if (moveDirection == Direction.LEFT) {
                        listener?.onStoryPreviousClicked(storyGroup, position)
                    } else if (moveDirection == Direction.RIGHT) {
                        listener?.onStoryNextClicked(storyGroup, position)
                    }
                }
                listener?.onResumeVideo(storyGroup, position)
                return true
            }
            else -> return true
        }
    }

    fun setController(storyDetailListener: StoryDetailListener?, position: Int, storyGroup: StoryGroup) {
        this.listener = storyDetailListener
        this.position = position
        this.storyGroup = storyGroup
    }

    override fun callOnClick(): Boolean {
        listener?.onStoryNextClicked(storyGroup, position)
        return true
    }
}

enum class Direction { UP, DOWN, LEFT, RIGHT }
class Point(val x: Float, val y: Float)