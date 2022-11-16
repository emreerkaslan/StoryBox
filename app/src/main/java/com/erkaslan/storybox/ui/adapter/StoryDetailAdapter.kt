package com.erkaslan.storybox.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.erkaslan.storybox.data.models.StoryGroup
import com.erkaslan.storybox.databinding.LayoutStoryDetailBinding

class StoryDetailAdapter() : ListAdapter<StoryGroup, RecyclerView.ViewHolder>(StoryDetailDiffCallback()) {

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
}

@SuppressLint("ClickableViewAccessibility")
class StoryDetailViewHolder(private val binding: LayoutStoryDetailBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(storyGroup: StoryGroup) {

    }
}