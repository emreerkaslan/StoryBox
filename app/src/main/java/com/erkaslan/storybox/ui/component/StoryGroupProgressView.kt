package com.erkaslan.storybox.ui.component

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import com.erkaslan.storybox.R

class StoryGroupProgressView : LinearLayout {

    companion object {
        const val SPACE = 5
        const val PROGRESS_LIMIT = 1000
    }

    private var totalBarCount: Int? = null
    private var currentStoryNumber: Int? = null
    private var currentTime: Int = 0

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    )

    fun initializeProgressView (totalBarCount: Int, currentStoryNumber: Int) {
        removeAllViews()
        this.totalBarCount = totalBarCount
        this.currentStoryNumber = currentStoryNumber
        repeat(totalBarCount) { it ->
            addProgressBar()
            if (it != totalBarCount) {
                addSpace()
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun addProgressBar() {
        val view = ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal)
        view.max = PROGRESS_LIMIT
        view.progressDrawable = context.getDrawable(R.drawable.progress_story)
        val params = LayoutParams(0, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        view.layoutParams = params
        addView(view)
    }

    private fun addSpace() {
        val spaceView = View(context).also { it.layoutParams = LayoutParams(SPACE, android.view.ViewGroup.LayoutParams.WRAP_CONTENT) }
        addView(spaceView)
    }
}