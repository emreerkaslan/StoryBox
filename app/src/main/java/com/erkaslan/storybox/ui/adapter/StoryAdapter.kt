package com.erkaslan.storybox.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.erkaslan.storybox.data.models.StoryGroup
import com.erkaslan.storybox.databinding.ColumnLayoutStoryBinding

class StoryAdapter (private val storyListener: StoryListener) : ListAdapter<StoryGroup, RecyclerView.ViewHolder>(StoryDiffCallBack()) {

    private class StoryDiffCallBack : DiffUtil.ItemCallback<StoryGroup>() {
        override fun areItemsTheSame(oldItem: StoryGroup, newItem: StoryGroup): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: StoryGroup, newItem: StoryGroup): Boolean =
            oldItem.username == newItem.username && oldItem.isAllStoriesWatched == newItem.isAllStoriesWatched && oldItem.storyList == newItem.storyList &&
                    oldItem.userAvatarUri == newItem.userAvatarUri
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return StoryViewHolder(
            ColumnLayoutStoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), storyListener
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as StoryViewHolder).bind(getItem(position))
    }

    inner class StoryViewHolder(private val binding: ColumnLayoutStoryBinding, private val listener: StoryListener) : RecyclerView.ViewHolder(binding.root) {
        fun bind(storyGroup: StoryGroup) {
            binding.story = storyGroup
            binding.root.setOnClickListener { listener.onStoryClicked(adapterPosition) }
        }
    }
}

interface StoryListener {
    fun onStoryClicked(storyIndex: Int)
}