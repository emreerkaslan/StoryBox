package com.erkaslan.storybox.ui.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.erkaslan.storybox.data.models.StoryGroup
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
            oldItem.isInvisible == newItem.isInvisible && oldItem.isAllStoriesWatched == newItem.isAllStoriesWatched && oldItem.username == newItem.username
                    && oldItem.storyList == newItem.storyList && oldItem.userAvatarUri == newItem.userAvatarUri
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
            Log.d("TEST", "bind")
            binding.storyGroup = storyGroup
            binding.svStoryGroup.setStoryGroup(storyGroup, listener, layoutPosition)
        }
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        Log.d("TEST", "detached")
        (holder as? StoryDetailViewHolder)?.binding?.svStoryGroup?.onDestroy()
    }
}

interface StoryDetailListener {
    fun onStoryNextClicked(storyGroup: StoryGroup?, position: Int?)
    fun onStoryPreviousClicked(storyGroup: StoryGroup?, position: Int?)
    fun onPauseVideo(storyGroup: StoryGroup?, position: Int?)
    fun onResumeVideo(storyGroup: StoryGroup?, position: Int?)
    fun onCloseStory(storyGroup: StoryGroup?, position: Int?)
}