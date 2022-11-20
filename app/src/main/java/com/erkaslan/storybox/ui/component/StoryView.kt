package com.erkaslan.storybox.ui.component

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.net.toUri
import com.erkaslan.storybox.R
import com.erkaslan.storybox.data.models.Story
import com.erkaslan.storybox.data.models.StoryGroup
import com.erkaslan.storybox.data.models.StoryType
import com.erkaslan.storybox.ui.adapter.StoryDetailListener
import com.erkaslan.storybox.util.DataBindingUtils
import com.erkaslan.storybox.util.GeneralUtils
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.StyledPlayerView

class StoryView : ConstraintLayout {
    private var storyImageView: ImageView = ImageView(context).also {
        it.layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        it.scaleType = ImageView.ScaleType.CENTER
    }

    private var storyVideoView = StyledPlayerView(context).also {
        it.useController = false
        it.visibility = GONE
        it.layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            bottomToBottom = R.id.sv_story_group
            topToTop = R.id.sv_story_group
        }
    }

    private var storyGroupProgressView: StoryGroupProgressView = StoryGroupProgressView(context).also {
        it.onPauseVideo = {
            currentTime = player?.currentPosition
            player?.pause()
        }
        it.layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        val margin25 = GeneralUtils.dpToPx(context.resources.getDimension(R.dimen.margin_25).toInt())
        it.setPadding(margin25, margin25, margin25, 0)
    }

    private var storyGroup: StoryGroup? = null
    private var listener: StoryDetailListener? = null
    private var adapterPosition: Int = 0
    private var player: ExoPlayer? = null
    private var currentTime: Long? = null

    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            if (isPlaying) {
                Log.d("TEST", "playing")
                Log.d("TEST", "position: " + player?.currentPosition)
                storyGroupProgressView.setAnimator(
                    duration = player?.duration
                        ?: StoryGroupProgressView.DEFAULT_DURATION
                )
                storyGroupProgressView.resumeProgress()
            } else Log.d("TEST", "paused")
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)
            when (playbackState) {
                Player.STATE_IDLE -> {
                    Log.d("TEST", "IDLE")
                }
                Player.STATE_READY -> {
                    Log.d("TEST", "READY")
                }
                Player.STATE_BUFFERING -> {
                    Log.d("TEST", "BUFFERING")
                }
                Player.STATE_ENDED -> {
                    Log.d("TEST", "ENDED")
                }
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            super.onPlayerError(error)
            Log.d("TEST", error.toString())
        }
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    )

    init {
        addView(storyImageView)
        addView(storyVideoView)
        addView(storyGroupProgressView)
    }

    fun setStoryGroup(storyGroup: StoryGroup, listener: StoryDetailListener?, adapterPosition: Int) {
        this.storyGroup = storyGroup
        this.listener = listener
        this.adapterPosition = adapterPosition

        setPlayer()

        val story = storyGroup.storyList[storyGroup.lastStoryIndex]
        when (story.type) {
            StoryType.IMAGE -> {
                player?.stop()
                storyGroupProgressView.initializeProgressView(storyGroup, adapterPosition, listener)
                setImage(story.mediaUri)
                if (!storyGroup.isPaused) storyGroupProgressView.resumeProgress()
                else storyGroupProgressView.pauseProgress()
            }

            StoryType.VIDEO -> {
                if (!storyGroup.isPaused) {
                    // video is different
                    if (player?.currentMediaItem != MediaItem.fromUri(story.mediaUri?.toUri() ?: Uri.EMPTY)) {
                        Log.d("TEST", "video different")
                        currentTime = 0
                        storyGroupProgressView.initializeProgressView(storyGroup, adapterPosition, listener)
                        setVideo(story)
                    }
                    // video is same
                    else {
                        Log.d("TEST", "video same: $currentTime")
                        // video not started
                        if ((currentTime ?: 0) <= 0) {
                            Log.d("TEST", "video not started: $currentTime")
                            player?.prepare()
                            player?.play()
                        }
                        // video started
                        else {
                            Log.d("TEST", "video started: $currentTime")
                            player?.prepare()
                            currentTime?.let { player?.seekTo(it) }
                            player?.play()
                        }
                    }
                } else {
                    Log.d("TEST", "player released")
                    storyGroupProgressView.pauseProgress()
                }
                storyVideoView.visibility = View.VISIBLE
                storyImageView.visibility = View.GONE
                storyVideoView.requestFocus()
            }
            else -> {}
        }
    }

    private fun setPlayer() {
        if (storyGroup?.isPaused == true) {
            Log.d("TEST", "player released")
            player?.release()
        } else {
            Log.d("TEST", player.toString())
            if (player == null) {
                Log.d("TEST", "player created")
                player = ExoPlayer.Builder(context).build()
                storyVideoView.player = player
            }
        }
    }

    private fun setImage(uri: String?) {
        DataBindingUtils.loadImageWithPlaceholder(storyImageView, uri, null)
        storyImageView.visibility = View.VISIBLE
        storyVideoView.visibility = View.GONE
    }

    private fun setVideo(story: Story) {
        story.mediaUri?.let {
            player?.setMediaItem(MediaItem.fromUri(it.toUri()))
            player?.prepare()
            Log.d("TEST", "listener added")
            player?.removeListener(playerListener)
            player?.addListener(playerListener)
            player?.play()
        }
    }
}