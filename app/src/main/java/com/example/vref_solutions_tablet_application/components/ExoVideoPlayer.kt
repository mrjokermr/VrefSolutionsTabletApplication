package com.example.vref_solutions_tablet_application.components

import android.content.Context
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.StyledPlayerView

@Composable
fun ExoVideoPlayer(videoPlayer: ExoPlayer?, modifier: Modifier, context: Context, doubleTapAction: ()-> Unit) {
    DisposableEffect(
        AndroidView(
            modifier = modifier.pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        doubleTapAction()
                    }
                )
            },
            factory = {
                StyledPlayerView(context).apply {
                    player = videoPlayer

                    useController = false
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL

                    FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            }
        )
    )
    {
        onDispose {
            if(videoPlayer != null) videoPlayer.release()
        }
    }
}