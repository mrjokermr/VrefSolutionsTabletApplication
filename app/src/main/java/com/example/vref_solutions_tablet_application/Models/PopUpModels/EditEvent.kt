package com.example.vref_solutions_tablet_application.Models.PopUpModels

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.example.vref_solutions_tablet_application.Enums.PopUpType
import com.example.vref_solutions_tablet_application.Handlers.CurrentTrainingHandler
import com.example.vref_solutions_tablet_application.ViewModels.LiveTrainingViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class EditEvent: BasePopUpScreen() {
    override val height: Dp = 580.dp
    override val width: Dp = 620.dp
    override val title: String = "Change event"
    override val confirmText: String = "Confirm changes"
    override val cancelText: String = "Cancel"
    override val type: PopUpType = PopUpType.EDIT_EVENT

    lateinit var viewModel: LiveTrainingViewModel

    val areYouSureInfo: MutableStateFlow<AreYouSureInfo?> = MutableStateFlow(null)

    fun ClearAreYouSureInfo() {
        areYouSureInfo.value = null
    }

    override fun Cancel() {
        //clear the given input because it was cancelled
        viewModel.SetEventTitle("")
        viewModel.SetOrResetDevidedFeedbackInfo(viewModel.devidedFeedbackContainer)

        viewModel.ClosePopUpScreen()
    }

    override fun Confirm() {
        //implementation here
        viewModel.ClosePopUpScreen()

        val currentTrainingHandler = CurrentTrainingHandler(currentContext = viewModel.context)

        viewModel.viewModelScope.launch {
            if(viewModel.uiEventToEdit.value != null) {
                viewModel.UpdateTrainingEvent(currentTrainingId = currentTrainingHandler.GetCurrentTrainingId(), authKey = currentTrainingHandler.GetAuthKey(),
                    trainingEventId = viewModel.uiEventToEdit.value!!.id.toString())

                //clear previous input
                viewModel.SetEventTitle("")
            }

        }
    }
}