package com.example.vref_solutions_tablet_application.models.popUpModels

import androidx.annotation.StringRes
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.vref_solutions_tablet_application.R
import com.example.vref_solutions_tablet_application.enums.PopUpType
import com.example.vref_solutions_tablet_application.models.CameraLink
import com.example.vref_solutions_tablet_application.models.CameraLinkObject
import com.example.vref_solutions_tablet_application.viewModels.LiveTrainingViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SwitchCamera(cameraType: PopUpType, var viewModel: LiveTrainingViewModel): BasePopUpScreen() {
    override val height: Dp = 580.dp
    override val width: Dp = 620.dp
    @StringRes
    override val title: Int = R.string.switch_camera
    @StringRes
    override val confirmText: Int = R.string.confirm
    @StringRes
    override val cancelText: Int = R.string.cancel
    override val type: PopUpType = cameraType

    var videoThatIsBeingDisplayedLinkObject: CameraLinkObject? = null

    var selectedCameraObject: MutableStateFlow<CameraLinkObject> = MutableStateFlow(CameraLink.cockPitMiddle)

    val allSwitchObjects: StateFlow<List<CameraLinkObject>> = MutableStateFlow(listOf(
        CameraLink.mapCamera,
        CameraLink.cockPitRight,
        CameraLink.cockPitMiddle,
        CameraLink.pilotsDiscussing,
        CameraLink.navigationAndAltitudeDisplay
    )
    )

    override fun cancel() {
        viewModel.closePopUpScreen()
    }

    override fun confirm() {
        if(type.equals(PopUpType.SWITCH_CAMERA_1)) viewModel.switchLargeVideoDisplay(selectedCameraObject.value)
        else viewModel.switchSmallVideoDisplay(selectedCameraObject.value)

        viewModel.closePopUpScreen()
    }

    fun setSelectedCameraObject(firstCameraObject: CameraLinkObject) {
        videoThatIsBeingDisplayedLinkObject = firstCameraObject
        selectedCameraObject.value = firstCameraObject
    }

    fun changeSelectedCamera(toBeSelectedCameraObject: CameraLinkObject) {
        selectedCameraObject.value = toBeSelectedCameraObject
    }
}