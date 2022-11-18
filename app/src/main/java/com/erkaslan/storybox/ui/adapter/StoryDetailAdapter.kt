package com.erkaslan.storybox.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
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

    inner class StoryDetailViewHolder(private val binding: LayoutStoryDetailBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(storyGroup: StoryGroup) {
            binding.storyGroup = storyGroup
            binding.pvStoryGroup.initializeProgressView(storyGroup.storyList.size, adapterPosition + 1)
            when (storyGroup.storyList[storyGroup.lastStoryIndex].type) {
                StoryType.IMAGE -> {
                    binding.imageStory = storyGroup.storyList[storyGroup.lastStoryIndex].mediaUri
                    binding.ivImageStory.setOnClickListener {
                        listener?.onStoryNextClicked(storyGroup, adapterPosition)
                    }
                    binding.ivImageStory.visibility = View.VISIBLE
                    binding.vvVideoStory.visibility = View.GONE
                }
                StoryType.VIDEO -> {
                    storyGroup.storyList[storyGroup.lastStoryIndex].mediaUri?.let {
                        binding.vvVideoStory.setVideoURI(it.toUri())
                        binding.vvVideoStory.visibility = View.VISIBLE
                        binding.ivImageStory.visibility = View.GONE
                        binding.vvVideoStory.setOnClickListener {
                            binding.vvVideoStory.setVideoURI(null)
                            listener?.onStoryNextClicked(storyGroup, adapterPosition)
                        }
                        binding.vvVideoStory.start()
                        binding.vvVideoStory.requestFocus()
                    }
                }
                else -> {}
            }
        }
    }
}

interface StoryDetailListener {
    fun onStoryNextClicked(storyGroup: StoryGroup, position: Int)
    fun onStoryPreviousClicked(storyGroup: StoryGroup, position: Int)
}