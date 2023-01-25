package com.example.vref_solutions_tablet_application.Models.PopUpModels

import android.app.Application
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.example.vref_solutions_tablet_application.Enums.PopUpType
import com.example.vref_solutions_tablet_application.Handlers.LoggedInUserHandler
import com.example.vref_solutions_tablet_application.Models.User
import com.example.vref_solutions_tablet_application.ViewModels.AdminsViewModel
import kotlinx.coroutines.launch

class EditUser(): BasePopUpScreen() {
    override val height: Dp = 382.dp
    override val width: Dp = 580.dp
    override val title: String = "Edit user"
    override val confirmText: String = "Confirm changes"
    override val cancelText: String = "Cancel"
    override val type: PopUpType = PopUpType.EDIT_USER

    lateinit var viewModel: AdminsViewModel
    lateinit var userToEdit: User

    override fun Cancel() {
        viewModel.ToggleDynamicPopUpScreen()
    }

    override fun Confirm() {
        //confirm edit user
        val context = viewModel.getApplication<Application>().baseContext
        viewModel.viewModelScope.launch {
            val loggedInUserHandler = LoggedInUserHandler(currentContext = context)
            viewModel.UpdateUser(context = context, authKey = loggedInUserHandler.GetAuthKey())
        }
    }

    fun SetBaseValues(_userToEdit: User, _viewModel: AdminsViewModel) {
        userToEdit = _userToEdit
        viewModel = _viewModel
    }
}