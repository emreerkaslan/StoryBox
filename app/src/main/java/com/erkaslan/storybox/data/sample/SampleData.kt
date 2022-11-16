package com.erkaslan.storybox.data.sample

import com.erkaslan.storybox.data.models.Story
import com.erkaslan.storybox.data.models.StoryGroup
import com.erkaslan.storybox.data.models.StoryType

object SampleData {
    val sampleStoryGroupList: List<StoryGroup> = listOf(
        StoryGroup(
            listOf(
                Story(
                    type = StoryType.IMAGE,
                    mediaUri = "https://upload.wikimedia.org/wikipedia/commons/8/83/Live.the_world_is_mine%2C.jpg"
                ),
                Story(
                    type = StoryType.IMAGE,
                    mediaUri = "https://upload.wikimedia.org/wikipedia/commons/8/8b/AaronBertram.jpg"
                ),
                Story(
                    type = StoryType.IMAGE,
                    mediaUri = "https://upload.wikimedia.org/wikipedia/commons/0/0c/Tribe_2007_%28rave_in_Brazil%29_12_%28344002854%29.jpg"
                )
            ),
            username = "david_g",
            userAvatarUri = "https://upload.wikimedia.org/wikipedia/commons/6/6f/David_Guetta_at_2011_MMVA.jpg",
        ),
        StoryGroup(
            listOf(
                Story(
                    type = StoryType.IMAGE,
                    mediaUri = "https://upload.wikimedia.org/wikipedia/commons/f/fa/Makeup-artist-in-udaipur.png"
                ),
                Story(
                    type = StoryType.VIDEO,
                    mediaUri = "https://upload.wikimedia.org/wikipedia/commons/1/19/SKYRISE_1.webm"
                ),
            ),
            username = "makeupeveryday",
            userAvatarUri = "https://upload.wikimedia.org/wikipedia/commons/f/f1/Photo-1520563683082-7ef74b616a89.jpg",
        ),
        StoryGroup(
            listOf(
                Story(
                    type = StoryType.IMAGE,
                    mediaUri = "https://upload.wikimedia.org/wikipedia/commons/5/54/EVD-tenis-050.jpg"
                ),
                Story(
                    type = StoryType.IMAGE,
                    mediaUri = "https://upload.wikimedia.org/wikipedia/commons/f/fb/Bolt_2007.2.jpg"
                ),
                Story(
                    type = StoryType.IMAGE,
                    mediaUri = "https://upload.wikimedia.org/wikipedia/commons/0/0a/Nike_Shox_NZ.jpg"
                ),
            ),
            username = "nike",
            userAvatarUri = "https://upload.wikimedia.org/wikipedia/commons/3/3f/Logo_NIKE.png",
        ),
        StoryGroup(
            listOf(
                Story(
                    type = StoryType.VIDEO,
                    mediaUri = "https://upload.wikimedia.org/wikipedia/commons/5/5e/Snapping_fingers.ogv"
                ),
                Story(
                    type = StoryType.VIDEO,
                    mediaUri = "https://upload.wikimedia.org/wikipedia/commons/2/28/%22Optimum_TT%22_laser_cutting_machine_during_the_%22Armiya_2021%22_exhibition.ogg"
                ),
                Story(
                    type = StoryType.VIDEO,
                    mediaUri = "https://upload.wikimedia.org/wikipedia/commons/2/28/TikTok_and_YouTube_Shorts_example.webm"
                ),
            ),
            username = "world_of_interests",
            userAvatarUri = "https://upload.wikimedia.org/wikipedia/commons/4/41/Adam_Kassab_SC_Code.jpg",
        ),
    )
}