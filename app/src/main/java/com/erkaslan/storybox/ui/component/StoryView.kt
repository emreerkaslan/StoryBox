package com.erkaslan.storybox.ui.component

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
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
import kotlin.math.abs

class StoryView : ConstraintLayout {

    companion object {
        const val SWIPE_THRESHOLD = 150
        const val STORY_TRANSITION_DIRECTION_THRESHOLD = 0.3
    }

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

    private var storyLoading = ProgressBar(context, null, android.R.attr.progressBarStyle).apply {
        visibility = VISIBLE
        indeterminateDrawable.setColorFilter(context.resources.getColor(R.color.deep_carmin_pink) ,android.graphics.PorterDuff.Mode.MULTIPLY)
        layoutParams = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            endToEnd = R.id.sv_story_group
            startToStart = R.id.sv_story_group
            bottomToBottom = R.id.sv_story_group
            topToTop = R.id.sv_story_group
        }
    }

    private var storyGroup: StoryGroup? = null
    private var listener: StoryDetailListener? = null
    private var adapterPosition: Int = 0
    private var player: ExoPlayer? = null
    private var currentTime: Long? = null

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
        addView(storyLoading)
    }

    fun setStoryGroup(storyGroup: StoryGroup, listener: StoryDetailListener?, adapterPosition: Int) {
        this.storyGroup = storyGroup
        this.listener = listener
        this.adapterPosition = adapterPosition

        val story = storyGroup.storyList[storyGroup.lastStoryIndex]
        setPlayer(story)
        setProgressView()

        when (story.type) {
            StoryType.IMAGE -> {
                player?.pause()
                if (!storyGroup.isInvisible) {
                    storyLoading.visibility = View.VISIBLE
                    storyGroupProgressView.initializeProgressView(storyGroup, adapterPosition, listener)
                    setImage(story.mediaUri)
                }
                else storyGroupProgressView.pauseProgress()
            }

            StoryType.VIDEO -> {
                // story group is visible
                if (!storyGroup.isInvisible) {
                    // different video
                    if (player?.currentMediaItem != MediaItem.fromUri(story.mediaUri?.toUri() ?: Uri.EMPTY)) {
                        Log.d("STORYBOX", "video different")
                        currentTime = 0
                        storyGroupProgressView.initializeProgressView(storyGroup, adapterPosition, listener)
                        storyGroupProgressView.pauseProgress()
                        setVideo(story)
                    }
                    // same video
                    else {
                        Log.d("STORYBOX", "video same: $currentTime")
                        // video not started
                        if ((currentTime ?: 0) <= 0) {
                            Log.d("STORYBOX", "video not started: $currentTime")
                            storyGroupProgressView.restart()
                            player?.prepare()
                            player?.play()
                        }
                        // video in middle
                        else {
                            Log.d("STORYBOX", "video started: $currentTime")
                            player?.prepare()
                            currentTime?.let { player?.seekTo(it) }
                            player?.play()
                        }
                    }
                }
                // story group is invisible
                else {
                    Log.d("STORYBOX", "player released")
                    storyGroupProgressView.pauseProgress()
                }

                storyVideoView.visibility = View.VISIBLE
                storyImageView.visibility = View.GONE
                storyVideoView.requestFocus()
            }
            else -> {}
        }
    }

    private fun setPlayer(story: Story) {
        if (storyGroup?.isInvisible == true || story.type == StoryType.IMAGE) {
            Log.d("STORYBOX", "player released")
            currentTime = 0
            player?.setMediaItem(MediaItem.fromUri(Uri.EMPTY))
        } else {
            if (player == null) {
                Log.d("STORYBOX", "player created")
                player = ExoPlayer.Builder(context).build()
                storyVideoView.player = player
            }
        }
    }

    private fun setProgressView() {
        if (storyGroup?.isInvisible == true) storyGroupProgressView.currentStoryIndex = -1
    }

    private fun setImage(uri: String?) {
        DataBindingUtils.loadImageWithPlaceholder(storyImageView, uri, null,
            onSuccess = {
                storyLoading.visibility = View.GONE
                storyGroupProgressView.resumeProgress()
            },
            onFailed = {
                listener?.onStoryNextClicked(storyGroup, adapterPosition)
            })
        storyImageView.visibility = View.VISIBLE
        storyVideoView.visibility = View.GONE
    }

    private fun setVideo(story: Story) {
        story.mediaUri?.let {
            player?.removeListener(playerListener)
            player?.addListener(playerListener)
            player?.setMediaItem(MediaItem.fromUri(it.toUri()))
            player?.prepare()
            player?.play()
        }
    }

    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            if (isPlaying) {
                Log.d("STORYBOX", "video playing")
                storyGroupProgressView.setAnimator(
                    duration = player?.duration
                        ?: StoryGroupProgressView.DEFAULT_DURATION
                )
                storyGroupProgressView.resumeProgress()
                storyLoading.visibility = View.GONE
            } else {
                Log.d("STORYBOX", "video paused")
                storyLoading.visibility = View.VISIBLE
            }
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)
            when (playbackState) {
                Player.STATE_IDLE -> {
                    Log.d("STORYBOX", "IDLE")
                }
                Player.STATE_READY -> {
                    Log.d("STORYBOX", "READY")
                }
                Player.STATE_BUFFERING -> {
                    Log.d("STORYBOX", "BUFFERING")
                    storyGroupProgressView.pauseProgress()
                    storyLoading.visibility = View.VISIBLE
                }
                Player.STATE_ENDED -> {
                    Log.d("STORYBOX", "ENDED")
                }
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            super.onPlayerError(error)
            Log.d("STORYBOX", "video error: $error")
        }
    }

    private var touchInitialTime = 0L
    private var touchFinalTime = 0L
    private var touchInitialPoint = Point(0F,0F)
    private var touchFinalPoint = Point(0F,0F)
    private var moveDirection: Direction = Direction.UP

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                Log.d("STORYBOX", "pointer down")
                touchInitialTime = System.currentTimeMillis()
                touchInitialPoint = Point(event.x, event.y)
                storyGroupProgressView.pauseProgress()
                storyGroup?.let {
                    if (it.storyList[it.lastStoryIndex].type == StoryType.VIDEO) {
                        currentTime = player?.currentPosition
                        player?.pause()
                    }
                }
                return true
            }
            MotionEvent.ACTION_UP -> {
                Log.d("STORYBOX", "pointer up")
                touchFinalTime = System.currentTimeMillis()
                val duration = touchFinalTime - touchInitialTime
                touchFinalPoint = Point(event.x, event.y)

                val deltaX = touchFinalPoint.x - touchInitialPoint.x
                val deltaY = touchFinalPoint.y - touchInitialPoint.y
                moveDirection =
                    if (abs(deltaY) * 0.5 < abs(deltaX)) {
                        if (deltaX > -30) Direction.RIGHT else Direction.LEFT
                    } else {
                        if(deltaY > 0) Direction.DOWN else Direction.UP
                    }

                if (moveDirection == Direction.DOWN) {
                    Log.d("STORYBOX", "swipe bottom")
                    player?.release()
                    storyGroupProgressView.resetAll()
                    listener?.onCloseStory(storyGroup, adapterPosition)
                    return true
                }

                if (duration < SWIPE_THRESHOLD) {
                    Log.d("STORYBOX", "tap")
                    storyGroupProgressView.resetAll()
                    return if ((touchFinalPoint.x + touchInitialPoint.x)/2 > this@StoryView.width * STORY_TRANSITION_DIRECTION_THRESHOLD) {
                        listener?.onStoryNextClicked(storyGroup, adapterPosition)
                        true
                    } else {
                        listener?.onStoryPreviousClicked(storyGroup, adapterPosition)
                        true
                    }
                } else {
                    Log.d("STORYBOX", "swipe horizontal")
                    listener?.onResumeVideo(storyGroup, adapterPosition)
                }
                return true
            }
            else -> return true
        }
    }

    fun onDestroy() {
        player?.removeListener(playerListener)
        player?.release()
        player = null
        listener = null
    }
}

enum class Direction { UP, DOWN, LEFT, RIGHT }
class Point(val x: Float, val y: Float)