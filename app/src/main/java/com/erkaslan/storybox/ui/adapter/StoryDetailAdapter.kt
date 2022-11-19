package com.erkaslan.storybox.ui.adapter

import android.annotation.SuppressLint
import android.util.Log
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
            Log.d("TEST", "bind: " + storyGroup.username + " lastItem: " + (storyGroup.lastStoryIndex + 1).toString())
            binding.storyGroup = storyGroup
            binding.pvStoryGroup.initializeProgressView(storyGroup, adapterPosition, listener)

            val story = storyGroup.storyList[storyGroup.lastStoryIndex]
            when (story.type) {
                StoryType.IMAGE -> {
                    binding.imageStory = story.mediaUri
                    binding.ivImageStory.visibility = View.VISIBLE
                    binding.vvVideoStory.visibility = View.GONE

                    if (story.isPaused) {
                        binding.pvStoryGroup.pauseAnimation()
                    } else {
                        if (storyGroup.lastStoryIndex != 0 || this@StoryDetailViewHolder.itemView.isAttachedToWindow)
                            binding.pvStoryGroup.startAnimation()
                    }
                }
                StoryType.VIDEO -> {
                    story.mediaUri?.let {
                        binding.vvVideoStory.setVideoURI(it.toUri())
                        binding.vvVideoStory.visibility = View.VISIBLE
                        binding.ivImageStory.visibility = View.GONE
                        binding.vvVideoStory.setOnCompletionListener {
                            listener?.onStoryNextClicked(storyGroup, adapterPosition)
                        }

                        if (story.isPaused) {
                            binding.vvVideoStory.pause()
                        } else {
                            if (binding.vvVideoStory.duration > 0) {
                                binding.vvVideoStory.resume()
                            } else {
                                if (storyGroup.lastStoryIndex != 0 || this@StoryDetailViewHolder.itemView.isAttachedToWindow) {
                                    binding.pvStoryGroup.startAnimation()
                                    binding.vvVideoStory.start()
                                }
                            }
                        }

                        binding.vvVideoStory.requestFocus()
                    }
                }
                else -> {}
            }
        }
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        Log.d("TEST", "attached: " + "bind: " + ((holder as StoryDetailViewHolder).binding?.storyGroup?.username ?: " ") + " lastItem: " + ((holder).binding?.storyGroup?.lastStoryIndex ?: 0 + 1).toString())
        val binding = (holder as StoryDetailViewHolder).binding
        if (binding.storyGroup?.storyList?.get(binding.storyGroup?.lastStoryIndex ?: 0)?.isPaused == false) {
            holder.binding.pvStoryGroup.startAnimation()
            if (binding.vvVideoStory.isVisible) holder.binding.vvVideoStory.start()
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
    fun onCloseStory(storyGroup: StoryGroup?, position: Int?)
}