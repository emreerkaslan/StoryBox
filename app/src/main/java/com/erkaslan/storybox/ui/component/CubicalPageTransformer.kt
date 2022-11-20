package com.erkaslan.storybox.ui.component

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs

class CubicalPageTransformer : ViewPager2.PageTransformer {
    companion object {
        const val deltaY = 0.5f
        const val rotationY = 22.5f
    }

    override fun transformPage(view: View, position: Float) {
        view.alpha = if (abs(position) >= 1f) 0f else 1f
        view.isEnabled = false
        view.pivotX = if (position < 0f) view.width.toFloat() else 0f
        view.pivotY = view.height * deltaY
        view.rotationY = rotationY * position
    }
}