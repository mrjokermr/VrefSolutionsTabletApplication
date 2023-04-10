package com.example.vref_solutions_tablet_application.models.popUpModels

import androidx.annotation.StringRes
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.vref_solutions_tablet_application.R
import com.example.vref_solutions_tablet_application.enums.PopUpType
import com.example.vref_solutions_tablet_application.viewModels.LiveTrainingViewModel

class PrescribedEventsDetails: BasePopUpScreen() {
    override val height: Dp = 580.dp
    override val width: Dp = 520.dp
    @StringRes
    override val title: Int = R.string.prescribed_events_details
    @StringRes
    override val confirmText: Int = R.string.empty_string
    @StringRes
    override val cancelText: Int = R.string.empty_string
    override val type: PopUpType = PopUpType.PRESCRIBED_EVENT_INFO

    lateinit var viewModel: LiveTrainingViewModel

    override fun cancel() {
        viewModel.closePopUpScreen()
    }

    override fun confirm() {

    }
}