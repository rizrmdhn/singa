package com.singa.asl.ui.components

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import kotlinx.coroutines.delay

/**
 * Composable function that displays an ExoPlayer to play a video using Jetpack Compose.
 *
 * @OptIn annotation to UnstableApi is used to indicate that the API is still experimental and may
 * undergo changes in the future.
 *
 */

@Composable
fun ExoPlayerView(
    exoPlayer: ExoPlayer,
    videoUrl: String,
    timeStamp:(millisecond:Long)->Unit,
) {

    // Get the current context

    // Create a MediaSource
    val mediaSource = remember(videoUrl) {
        MediaItem.fromUri(videoUrl)
    }

    // Set MediaSource to ExoPlayer
    LaunchedEffect(mediaSource) {
        exoPlayer.setMediaItem(mediaSource)
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
    }

    val playbackPosition = remember { mutableLongStateOf(0L) }

    // Manage lifecycle events
    DisposableEffect(Unit) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                    // Log and update playback position while playing
                    playbackPosition.longValue = exoPlayer.currentPosition
                    Log.d("ExoPlayerView", "Playback is Playing position: ${playbackPosition.longValue} ms")
                    timeStamp(playbackPosition.longValue)
            }

            override fun onPlaybackStateChanged(state: Int) {
                playbackPosition.longValue = exoPlayer.currentPosition
                Log.d("ExoPlayerView", "Playback position: ${playbackPosition.longValue} ms")
                timeStamp(playbackPosition.longValue)

                when (state) {
                    Player.STATE_READY -> {
                        Log.d("Player", "STATE_READY- duration: ${playbackPosition.longValue}") // <----- Problem 2
                    }

                    Player.STATE_ENDED -> {
                        Log.d("Player", "STATE_ENDED")
                    }

                    Player.STATE_BUFFERING, Player.STATE_IDLE -> {
                        Log.d("Player", "STATE_BUFFERING or STATE_IDLE")
                    }
                }
            }

        }

        exoPlayer.addListener(listener)

        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }

    LaunchedEffect(
        Unit
    ) {
        while(true){
            Log.i("ExoPlayerView", "Current position: ${playbackPosition.longValue}")
            delay(1000)
            if(playbackPosition.longValue != exoPlayer.currentPosition){
                timeStamp(playbackPosition.longValue)
            }

            playbackPosition.longValue = exoPlayer.currentPosition
        }
    }


    // Use AndroidView to embed an Android View (PlayerView) into Compose
    AndroidView(
        factory = { ctx ->
            PlayerView(ctx).apply {
                player = exoPlayer
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                shape = RoundedCornerShape(16.dp)
            )
            .height(200.dp) // Set your desired height
    )

}