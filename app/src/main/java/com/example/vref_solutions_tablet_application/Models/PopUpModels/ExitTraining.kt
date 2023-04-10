package com.example.vref_solutions_tablet_application.models.popUpModels

import android.app.Application
import androidx.annotation.StringRes
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.vref_solutions_tablet_application.R
import com.example.vref_solutions_tablet_application.enums.PopUpType
import com.example.vref_solutions_tablet_application.handlers.CurrentTrainingHandler
import com.example.vref_solutions_tablet_application.ScreenNavName
import com.example.vref_solutions_tablet_application.viewModels.LiveTrainingViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class ExitTraining: BasePopUpScreen() {

    override val height: Dp = 440.dp
    override val width: Dp = 520.dp
    @StringRes
    override val title: Int = R.string.exit_training
    @StringRes
    override val confirmText: Int = R.string.exit_training
    @StringRes
    override val cancelText: Int = R.string.cancel
    override val type: PopUpType = PopUpType.EXIT_TRAINING

    lateinit var viewModel: LiveTrainingViewModel

    var areYouSureScreenOpen = MutableStateFlow(false)
    var areYouSureInfo = MutableStateFlow(
        AreYouSureInfo(popUptitle = "Error",
        popUpexplanation = AnnotatedString(text = "Error"),
        confirmActionText = "Confirm",
        cancelActionText = "Cancel",
        width = 0.dp,
        height = 0.dp,
        isDiscardTraining = false,
        isFinishTraining = false,
        isDeleteUser = false)
    )

    override fun cancel() {
        viewModel.closePopUpScreen()
    }

    override fun confirm() {
        val currentTrainingHandler = CurrentTrainingHandler(currentContext = viewModel.getApplication<Application>().baseContext)
        //implementation here
        if(areYouSureInfo.value.isFinishTraining) {
            //finish the training
            viewModel.finishCurrentTraining()
        }
        else if(!areYouSureInfo.value.isDiscardTraining && !areYouSureInfo.value.isFinishTraining) {
            //navigate to main menu without finishing the current training
            viewModel.navigateToPage(ScreenNavName.MainMenu)

        }
        else if(areYouSureInfo.value.isDiscardTraining){
            //discard the training and navigate to the main menu
            viewModel.launchDeleteTraining(currentTrainingHandler = currentTrainingHandler)
        }
    }

    fun closeAreYouSurePopUp() {
        areYouSureScreenOpen.value = false
    }

    fun setAndDisplayAreYouSurePopUp(infoClass: AreYouSureInfo) {
        areYouSureInfo.value = infoClass
        areYouSureScreenOpen.value = true
    }
}