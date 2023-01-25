package com.example.vref_solutions_tablet_application.Models.PopUpModels

import android.app.Application
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.example.vref_solutions_tablet_application.Enums.PopUpType
import com.example.vref_solutions_tablet_application.Handlers.CurrentTrainingHandler
import com.example.vref_solutions_tablet_application.ScreenNavName
import com.example.vref_solutions_tablet_application.ViewModels.LiveTrainingViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ExitTraining: BasePopUpScreen() {

    override val height: Dp = 440.dp
    override val width: Dp = 520.dp
    override val title: String = "Exit training"
    override val confirmText: String = "Exit Training"
    override val cancelText: String = "Cancel"
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

    override fun Cancel() {
        viewModel.ClosePopUpScreen()
    }

    override fun Confirm() {
        val currentTrainingHandler = CurrentTrainingHandler(currentContext = viewModel.context)
        //implementation here
        if(areYouSureInfo.value.isFinishTraining) {
            //finish the training
            viewModel.FinishCurrentTraining(context = viewModel.getApplication<Application>().baseContext)
        }
        else if(!areYouSureInfo.value.isDiscardTraining && !areYouSureInfo.value.isFinishTraining) {
            //navigate to main menu without finishing the current training

            viewModel.NavigateToPage(ScreenNavName.MainMenu)

        }
        else if(areYouSureInfo.value.isDiscardTraining){
            //discard the training and navigate to the main menu
            viewModel.viewModelScope.launch {
                viewModel.DeleteTraining(targetTrainingId = currentTrainingHandler.GetCurrentTrainingId(), authKey = currentTrainingHandler.GetAuthKey())

                viewModel.NavigateToPage(ScreenNavName.MainMenu)
            }
        }


    }

    fun CloseAreYouSurePopUp() {
        areYouSureScreenOpen.value = false
    }

    fun SetAndDisplayAreYouSurePopUp(infoClass: AreYouSureInfo) {
        areYouSureInfo.value = infoClass
        areYouSureScreenOpen.value = true
    }
}