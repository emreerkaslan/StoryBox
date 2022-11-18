package com.erkaslan.storybox.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.erkaslan.storybox.data.models.StoryGroup
import com.erkaslan.storybox.data.models.StoryType
import com.erkaslan.storybox.databinding.LayoutStoryDetailBinding

class StoryDetailAdapter : ListAdapter<StoryGroup, RecyclerView.ViewHolder>(StoryDetailDiffCallback()) {

    private var listener: StoryDetailListener? = null

    fun setStoryDetailListener(storyDetailListener: StoryDetailListener) {
        this.listener = storyDetailListener
    }

    private class StoryDetailDiffCallback : DiffUtil.ItemCallback<StoryGroup>() {
        override fun areItemsTheSame(oldItem: StoryGroup, newItem: StoryGroup): Boolean =
            oldItem == newItem

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: StoryGroup, newItem: StoryGroup): Boolean =
            oldItem.username == newItem.username && oldItem.isAllStoriesWatched == newItem.isAllStoriesWatched && oldItem.storyList == newItem.storyList &&
                    oldItem.userAvatarUri == newItem.userAvatarUri
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryDetailViewHolder {
        return StoryDetailViewHolder(
            LayoutStoryDetailBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as StoryDetailViewHolder).bind(getItem(position))
    }

    inner class StoryDetailViewHolder(val binding: LayoutStoryDetailBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(storyGroup: StoryGroup) {
            binding.storyGroup = storyGroup
            binding.pvStoryGroup.initializeProgressView(storyGroup.storyList.size, adapterPosition + 1)
            binding.clStoryTouchController.setController(listener, adapterPosition, storyGroup)
            val story = storyGroup.storyList[storyGroup.lastStoryIndex]
            when (story.type) {
                StoryType.IMAGE -> {
                    binding.imageStory = story.mediaUri
                    binding.clStoryTouchController.setOnTouchListener { view, motionEvent ->
                        view.onTouchEvent(motionEvent)
                    }
                    binding.ivImageStory.visibility = View.VISIBLE
                    binding.vvVideoStory.visibility = View.GONE
                }
                StoryType.VIDEO -> {
                    story.mediaUri?.let {
                        binding.vvVideoStory.setVideoURI(it.toUri())
                        if (story.isPaused) binding.vvVideoStory.pause()
                        else {
                            if (binding.vvVideoStory.duration > 0) {
                                binding.vvVideoStory.resume()
                            } else {
                                if (storyGroup.lastStoryIndex != 0) binding.vvVideoStory.start()
                            }
                        }
                        binding.vvVideoStory.visibility = View.VISIBLE
                        binding.ivImageStory.visibility = View.GONE
                        binding.vvVideoStory.requestFocus()
                    }
                }
                else -> {}
            }
        }
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        val binding = (holder as StoryDetailViewHolder).binding
        if (binding.vvVideoStory.isVisible && binding.storyGroup?.storyList?.get(binding.storyGroup?.lastStoryIndex ?: 0)?.isPaused == false) {
            holder.binding.vvVideoStory.start()
        }
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        if (!(holder as StoryDetailViewHolder).binding.vvVideoStory.isVisible) {
            holder.binding.vvVideoStory.pause()
        }
    }
}

interface StoryDetailListener {
    fun onStoryNextClicked(storyGroup: StoryGroup?, position: Int?)
    fun onStoryPreviousClicked(storyGroup: StoryGroup?, position: Int?)
    fun onPauseVideo(storyGroup: StoryGroup?, position: Int?)
    fun onResumeVideo(storyGroup: StoryGroup?, position: Int?)
    fun onCloseStory()
}