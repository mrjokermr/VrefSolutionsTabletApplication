package com.example.vref_solutions_tablet_application.models.popUpModels

import androidx.annotation.StringRes
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.vref_solutions_tablet_application.R
import com.example.vref_solutions_tablet_application.enums.PopUpType
import com.example.vref_solutions_tablet_application.viewModels.LiveTrainingViewModel

class AddEvent: BasePopUpScreen() {
    override val height: Dp = 580.dp
    override val width: Dp = 620.dp
    @StringRes
    override val title: Int = R.string.add_new_event
    @StringRes
    override val confirmText: Int = R.string.add_event
    @StringRes
    override val cancelText: Int = R.string.cancel
    override val type: PopUpType = PopUpType.ADD_EVENT

    lateinit var viewModel: LiveTrainingViewModel

    override fun cancel() {
        //clear the given input because it was cancelled
        viewModel.setEventTitle("")
        //viewModel.SetEventDescription("")

        viewModel.closePopUpScreen()
    }

    override fun confirm() {
        //implementation here
        viewModel.closePopUpScreen()

        viewModel.launchNewTrainingEvent()
    }
}