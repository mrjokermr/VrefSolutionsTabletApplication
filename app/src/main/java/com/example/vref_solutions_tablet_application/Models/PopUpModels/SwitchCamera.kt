package com.example.vref_solutions_tablet_application.Models.PopUpModels

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.vref_solutions_tablet_application.Enums.PopUpType
import com.example.vref_solutions_tablet_application.Models.CameraLink
import com.example.vref_solutions_tablet_application.Models.CameraLinkObject
import com.example.vref_solutions_tablet_application.ViewModels.LiveTrainingViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SwitchCamera(cameraType: PopUpType, var viewModel: LiveTrainingViewModel): BasePopUpScreen() {
    override val height: Dp = 580.dp
    override val width: Dp = 620.dp
    override val title: String = "Switch camera"
    override val confirmText: String = "Confirm"
    override val cancelText: String = "Cancel"
    override val type: PopUpType = cameraType

    //Waarom geen lateinit ipv var, als ik lateinit zou gebruiken krijg ik de error :
    //lateinit property videoThatIsBeingDisplayedLinkObject has not been initialized
    //terwijl ik deze vlak voor t aanroepen van t videoswitchcamera component door middel van de SetSelectedCameraObject methode wel set
    //dus deze SwitchCameraPopUpComponent wordt te snel aangeroepen dus ipv een hele delay in te bouwen is de ? = null oplossing beter
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

    override fun Cancel() {
        viewModel.ClosePopUpScreen()
    }

    override fun Confirm() {
        if(type.equals(PopUpType.SWITCH_CAMERA_1)) viewModel.SwitchLargeVideoDisplay(selectedCameraObject.value)
        else viewModel.SwitchSmallVideoDisplay(selectedCameraObject.value)

        viewModel.ClosePopUpScreen()
    }

    fun SetSelectedCameraObject(firstCameraObject: CameraLinkObject) {
        videoThatIsBeingDisplayedLinkObject = firstCameraObject
        selectedCameraObject.value = firstCameraObject
    }

    fun ChangeSelectedCamera(toBeSelectedCameraObject: CameraLinkObject) {
        selectedCameraObject.value = toBeSelectedCameraObject
    }
}