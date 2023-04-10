package com.example.vref_solutions_tablet_application.api.requestBodies

data class CamerasRequestBody(
    val cameras: List<CameraRequest> = listOf(CameraRequest())
)

data class CameraRequest(
    val name: String = "string",
    val url: String = "string",
    val username: String = "string",
    val password: String = "string",
    val captureMode: String = "Video",
)
