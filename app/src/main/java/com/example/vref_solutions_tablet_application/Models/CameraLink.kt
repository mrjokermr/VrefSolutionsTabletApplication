package com.example.vref_solutions_tablet_application.Models

import com.example.vref_solutions_tablet_application.Enums.VideoDisplayType
import com.example.vref_solutions_tablet_application.R

class CameraLink {
    companion object {
        //Streamable link can become invalid, switch it up for:
        //video urls that work but cause Exoplayer to create a small memory leak when switching cameraLinkObjects
//        cockpit_right.mp4
//        https://ffmpegstoragetest.blob.core.windows.net/con/Melle/cockpit_right.mp4
//
//        navigational_display_and_altiude.mp4
//        https://ffmpegstoragetest.blob.core.windows.net/con/Melle/navigational_display_and_altiude.mp4
//
//        cockpit_middle.mp4
//        https://ffmpegstoragetest.blob.core.windows.net/con/Melle/cockpit_middle.mp4
//
//        pilots_discussing.mp4
//        https://ffmpegstoragetest.blob.core.windows.net/con/Melle/pilots_discussing.mp4
//
//        fly_map.mp4
//        https://ffmpegstoragetest.blob.core.windows.net/con/Melle/fly_map.mp4
        val cockPitRight: CameraLinkObject =
            CameraLinkObject(url = "https://cdn-cf-east.streamable.com/video/mp4/f5058v.mp4?Expires=1674818100&Signature=jqatKczx~Ol-vUXGvc0W7wnLz9d0OBvdUAeyxSeefMu0x234-zAGeE5SP~SiYIUkjawdyAHj3FK4yUash39wJbHqpQxfU3jldnGh6aUoysC-TkPYG-UCuAY4yKqz5bmIi5UrH2PwG3GAs3eD9ZghI8huJRFqHY5-8azEbXP84psc-1rNXXCP4oaH6dQlCCgtO3QovbXNOFm4TfJoyeaqXMiNkIAgVJBb~71S5wjefamhtOVEct0aSXPFcc2dofink5plD4MaDW8Fh2eyysirq7rDxgVrgut9t7gX5-h7vd0OWZhqUZwKhvcWhl6IofZoXSDFlRaHM3SbOYmznLp0Mw__&Key-Pair-Id=APKAIEYUVEN4EVB2OKEQ", thumbnailDrawableId = R.drawable.thumbnail_cockpit_right,
                videoDisplayType = VideoDisplayType.COCKPIT_RIGHT)
        val mapCamera: CameraLinkObject =
            CameraLinkObject(url = "https://cdn-cf-east.streamable.com/video/mp4/dbdnzp.mp4?Expires=1674818100&Signature=HdbucJlGWodVVtG5WVINaVUno-O9sU9wHQ-0ZBCqgE1YpVA3evl-2m6XtI0igRPk1UTMhl-KxJee1-MVWq9cFe-~4t54S5TlexNQzINz29lgo4NbHgNTxzeQQSAWrq75hgcJSKRvQ0KqKCLxATS3~6B-UBPVDDTT6LCIulYs3yUQub8y1UqIf1w58Y4a8OZeNWGnQiybzraL-S35fECPEH-7QUju6UMq7cX6EcFHIPBbdOBCE3FV746eiKyL3eD~wLSd7-3b8EWlKFhh5vuFzM-Udp12BfDKjwnO~M4PBbmCVoFFUhA2bKg3ZvhrMDmbrf~de5N3afk8vwmJNqtniA__&Key-Pair-Id=APKAIEYUVEN4EVB2OKEQ", thumbnailDrawableId = R.drawable.thumbnail_fly_map,
                videoDisplayType = VideoDisplayType.MAP_CAMERA)
        val navigationAndAltitudeDisplay: CameraLinkObject =
            CameraLinkObject(url = "https://cdn-cf-east.streamable.com/video/mp4/ek45jb.mp4?Expires=1674818100&Signature=jKEMzeHabr3vyHLVrwt7E5HsThG-83FEDVs8Br-u7oymGvPEAVAARa01~sGurO1Vvl35TnsDARQph071dz273HP6hOVqNlFQF1co6JoANz0BTJvyH-6dvwQ60VW~jxNefYSfbAR3ZL4Rxqrvz1SHUN4h~XYTI6exzicBjcPO-L0az0MZzhQTQA2ZoNNgGJ6l1ofsnJj8eW08YNQCJv5zKWX58rR4nQss9KiFc5eGaWTnoh-FO3X1THpjmM2aB1O4EBDMqzd7QE93WIrgYLmreMpRn03NexbvvPoZd6Jr2~cEu8XOd1S6KEIhR3axHWkQTFCAcqsmKDQj1VNYAokn6w__&Key-Pair-Id=APKAIEYUVEN4EVB2OKEQ", thumbnailDrawableId = R.drawable.thumbnail_navigation_altitude,
                videoDisplayType = VideoDisplayType.NAVIGATION_AND_ALTITUDE)
        val cockPitMiddle: CameraLinkObject =
            CameraLinkObject(url = "https://cdn-cf-east.streamable.com/video/mp4/071mup.mp4?Expires=1674815400&Signature=ECR6nOyg9t~A6G9b4IhrN31IAkjUMSUosv7RrtZcZDdJ~c6tn390BfzUdXYsYZLfVa58UUkUyruYW3X0xd4~YrOs2BpNnfukHscTlXIuNf2CC7zZxsargVMoekKxtcqtCL-ThpCdkhUFL-D3Gk0~bKTi85ph5DzYGGoRR17H1EQdfttafcO~WEr7C8-fC5guBiYuaOr2ufMRieyCHmEgKUZS~Y6uC1D0Twy~~vxVi83GERbSNOADG0uefsU1-izPKRk8OTTlhEpxW2-pDD4EEbYAW2WjscNu4ZUI1FobssGngjt7-AyaZzH6us-oPJAZrM0HnoORRmblmiR0GyEZIA__&Key-Pair-Id=APKAIEYUVEN4EVB2OKEQ", thumbnailDrawableId = R.drawable.thumbnail_cockpit_middle,
                videoDisplayType = VideoDisplayType.COCKPIT_MIDDLE)
        val pilotsDiscussing: CameraLinkObject =
            CameraLinkObject(url = "https://cdn-cf-east.streamable.com/video/mp4/bqqu0w.mp4?Expires=1674818100&Signature=hLGxJpF67f8HXkr3DRCakHH3QhHWt~5MrDdz88LG1jkbiylkq6MgrHfSNLGjfXS1kpNEvl~4Tonx5hXYq0GJejxAW6BKWP04rzrtM~KI~Aicj1jGr98jm0fcvRklHqYzU16Pap-ZIApQl1mWSkAc-TZ7vcM8~~VqfkMZiXW2ZpAOKsSX~ExvKJ4mpmao5mUv3z7nd3i0n2WaqWK71-Kv169dG47SomAg7j0scwgVqQnEG8SShbGb69vTI9JrqqWW0p5s6JFyr4V-svNaNqs-MqJF9byhRxtzWKtFg8uexluUYQsKMCnvoZD-aSmd9da6z5ROzAdMBabkdQ-GmRMgJA__&Key-Pair-Id=APKAIEYUVEN4EVB2OKEQ", thumbnailDrawableId = R.drawable.thumbnail_pilots_discussing,
                videoDisplayType = VideoDisplayType.PILOTS_DISCUSSING)
    }
}