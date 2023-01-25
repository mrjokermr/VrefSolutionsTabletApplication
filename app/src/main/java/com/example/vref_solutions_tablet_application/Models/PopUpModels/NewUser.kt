package com.example.vref_solutions_tablet_application.Models.PopUpModels

import android.app.Application
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.example.vref_solutions_tablet_application.Enums.PopUpType
import com.example.vref_solutions_tablet_application.Handlers.LoggedInUserHandler
import com.example.vref_solutions_tablet_application.ViewModels.AdminsViewModel
import kotlinx.coroutines.launch

class NewUser(): BasePopUpScreen() {
    override val height: Dp = 382.dp
    override val width: Dp = 580.dp
    override val title: String = "New user"
    override val confirmText: String = "Add new user"
    override val cancelText: String = "Cancel"
    override val type: PopUpType = PopUpType.NEW_USER

    lateinit var viewModel: AdminsViewModel

    override fun Cancel() {
        viewModel.ToggleDynamicPopUpScreen()
    }

    override fun Confirm() {
        val context = viewModel.getApplication<Application>().baseContext
        val loggedInUserHandler = LoggedInUserHandler(currentContext = context)

        viewModel.viewModelScope.launch {
            viewModel.CreateNewUser(context = context, authKey = loggedInUserHandler.GetAuthKey())


        }

    }
}