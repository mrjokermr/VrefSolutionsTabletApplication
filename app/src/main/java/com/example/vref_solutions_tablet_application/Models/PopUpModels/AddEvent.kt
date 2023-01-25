package com.example.vref_solutions_tablet_application.Models.PopUpModels

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.example.vref_solutions_tablet_application.Enums.PopUpType
import com.example.vref_solutions_tablet_application.Handlers.CurrentTrainingHandler
import com.example.vref_solutions_tablet_application.ViewModels.LiveTrainingViewModel
import kotlinx.coroutines.launch

class AddEvent: BasePopUpScreen() {
    override val height: Dp = 580.dp
    override val width: Dp = 620.dp
    override val title: String = "Add new event"
    override val confirmText: String = "Add event"
    override val cancelText: String = "Cancel"
    override val type: PopUpType = PopUpType.ADD_EVENT

    lateinit var viewModel: LiveTrainingViewModel

    override fun Cancel() {
        //clear the given input because it was cancelled
        viewModel.SetEventTitle("")
        //viewModel.SetEventDescription("")

        viewModel.ClosePopUpScreen()
    }

    override fun Confirm() {
        //implementation here
        viewModel.ClosePopUpScreen()

        val currentTrainingHandler = CurrentTrainingHandler(currentContext = viewModel.context)

        viewModel.viewModelScope.launch {
            viewModel.NewTrainingEvent(currentTrainingId = currentTrainingHandler.GetCurrentTrainingId(), authKey = currentTrainingHandler.GetAuthKey())
        }

    }
}