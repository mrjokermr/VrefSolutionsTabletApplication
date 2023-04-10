package com.example.vref_solutions_tablet_application.models.popUpModels

import androidx.annotation.StringRes
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.vref_solutions_tablet_application.R
import com.example.vref_solutions_tablet_application.enums.PopUpType
import com.example.vref_solutions_tablet_application.viewModels.LiveTrainingViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class EditEvent: BasePopUpScreen() {
    override val height: Dp = 580.dp
    override val width: Dp = 620.dp
    @StringRes
    override val title: Int = R.string.change_event
    @StringRes
    override val confirmText: Int = R.string.confirm_changes
    @StringRes
    override val cancelText: Int = R.string.cancel
    override val type: PopUpType = PopUpType.EDIT_EVENT

    lateinit var viewModel: LiveTrainingViewModel

    val areYouSureInfo: MutableStateFlow<AreYouSureInfo?> = MutableStateFlow(null)

    fun clearAreYouSureInfo() {
        areYouSureInfo.value = null
    }

    override fun cancel() {
        //clear the given input because it was cancelled
        viewModel.setEventTitle("")
        viewModel.setOrResetDevidedFeedbackInfo(viewModel.devidedFeedbackContainer)

        viewModel.closePopUpScreen()
    }

    override fun confirm() {
        //implementation here
        viewModel.closePopUpScreen()
        viewModel.launchUpdateTrainingEvent()
    }
}