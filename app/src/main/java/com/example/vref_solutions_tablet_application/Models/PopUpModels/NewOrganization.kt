package com.example.vref_solutions_tablet_application.Models.PopUpModels

import android.app.Application
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.example.vref_solutions_tablet_application.Enums.PopUpType
import com.example.vref_solutions_tablet_application.ViewModels.AdminsViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NewOrganization(): BasePopUpScreen() {
    override val height: Dp = 262.dp
    override val width: Dp = 580.dp
    override val title: String = "New organization"
    override val confirmText: String = "Confirm"
    override val cancelText: String = "Cancel"
    override val type: PopUpType = PopUpType.NEW_ORGANIZATION

    lateinit var viewModel: AdminsViewModel
    lateinit var authToken: String

    private val _inputNewOrganizationName = MutableStateFlow("")
    val inputNewOrganizationName: StateFlow<String> = _inputNewOrganizationName

    override fun Cancel() {
        viewModel.ToggleDynamicPopUpScreen()
    }

    override fun Confirm() {
        if(_inputNewOrganizationName.value.length >= 3) {
            viewModel.viewModelScope.launch {
                viewModel.CreateNewOrganization(
                    authKey = authToken,
                    context = viewModel.getApplication<Application>().baseContext,
                    organizationName = _inputNewOrganizationName.value
                )
            }
        }
    }

    fun SetOrganizationValue(value: String) {
        _inputNewOrganizationName.value = value
    }
}