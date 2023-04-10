package com.example.vref_solutions_tablet_application.models.popUpModels

import androidx.annotation.StringRes
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.vref_solutions_tablet_application.R
import com.example.vref_solutions_tablet_application.enums.PopUpType
import com.example.vref_solutions_tablet_application.viewModels.AdminsViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class NewOrganization(): BasePopUpScreen() {
    override val height: Dp = 262.dp
    override val width: Dp = 580.dp
    @StringRes
    override val title: Int = R.string.new_organization
    @StringRes
    override val confirmText: Int = R.string.confirm
    @StringRes
    override val cancelText: Int = R.string.cancel
    override val type: PopUpType = PopUpType.NEW_ORGANIZATION

    lateinit var viewModel: AdminsViewModel
    lateinit var authToken: String

    private val _inputNewOrganizationName = MutableStateFlow("")
    val inputNewOrganizationName: StateFlow<String> = _inputNewOrganizationName

    override fun cancel() {
        viewModel.toggleDynamicPopUpScreen()
    }

    override fun confirm() {
        if(_inputNewOrganizationName.value.length >= 3) {
            viewModel.launchCreateNewOrganization(authKey = authToken, organizationName = _inputNewOrganizationName.value)
        }
    }

    fun setOrganizationValue(value: String) {
        _inputNewOrganizationName.value = value
    }
}