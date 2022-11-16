package com.erkaslan.storybox.data.models

data class Story(
    val type: StoryType? = StoryType.IMAGE,
    val durationInMillis: Int? = 5000,
    val mediaUri: String? = null
)

data class StoryGroup(
    val storyList: List<Story> = listOf(),
    val username: String? = null,
    val userAvatarUri: String? = null,
    val lastStoryIndex: Int = 0,
    val isAllStoriesWatched: Boolean = false
)

enum class StoryType {
    IMAGE, VIDEO
}