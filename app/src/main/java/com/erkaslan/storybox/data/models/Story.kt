package com.erkaslan.storybox.data.models

data class Story(
    val type: StoryType? = StoryType.IMAGE,
    var isPaused: Boolean = false,
    val durationInMillis: Int? = 5000,
    val mediaUri: String? = null
)

data class StoryGroup(
    val storyList: List<Story> = listOf(),
    val username: String? = null,
    val userAvatarUri: String? = null,
    var lastStoryIndex: Int = 0,
    var isAllStoriesWatched: Boolean = false
)

enum class StoryType {
    IMAGE, VIDEO,
}