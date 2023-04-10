package com.example.vref_solutions_tablet_application.handlers

import com.example.vref_solutions_tablet_application.models.CameraLinkObject
import com.google.android.exoplayer2.ExoPlayer

//previously used this videoplayer but switched it up because the Exoplayer SetMediaItem() was working but not updating in the front-end
class VideoDisplayer(videoPlayer: ExoPlayer, initialCameraLinkObject: CameraLinkObject) {

    var videoPlayer: ExoPlayer = videoPlayer
    var activeCameraLinkObject: CameraLinkObject = initialCameraLinkObject
}

