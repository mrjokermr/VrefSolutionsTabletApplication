package com.example.vref_solutions_tablet_application.Models.PopUpModels

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.example.vref_solutions_tablet_application.Enums.PopUpType
import com.example.vref_solutions_tablet_application.Handlers.CurrentTrainingHandler
import com.example.vref_solutions_tablet_application.ViewModels.LiveTrainingViewModel
import kotlinx.coroutines.launch

class PrescribedEventsDetails: BasePopUpScreen() {
    override val height: Dp = 580.dp
    override val width: Dp = 520.dp
    override val title: String = "Prescribed events details"
    override val confirmText: String = ""
    override val cancelText: String = ""
    override val type: PopUpType = PopUpType.PRESCRIBED_EVENT_INFO

    lateinit var viewModel: LiveTrainingViewModel

    override fun Cancel() {
        viewModel.ClosePopUpScreen()
    }

    override fun Confirm() {

    }
}