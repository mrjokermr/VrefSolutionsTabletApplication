package com.example.vref_solutions_tablet_application.models.popUpModels

import android.app.Application
import androidx.annotation.StringRes
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.vref_solutions_tablet_application.R
import com.example.vref_solutions_tablet_application.enums.PopUpType
import com.example.vref_solutions_tablet_application.handlers.LoggedInUserHandler
import com.example.vref_solutions_tablet_application.models.User
import com.example.vref_solutions_tablet_application.viewModels.AdminsViewModel

class EditUser(): BasePopUpScreen() {
    override val height: Dp = 382.dp
    override val width: Dp = 580.dp
    @StringRes
    override val title: Int = R.string.edit_user
    @StringRes
    override val confirmText: Int = R.string.confirm_changes
    @StringRes
    override val cancelText: Int = R.string.cancel
    override val type: PopUpType = PopUpType.EDIT_USER

    lateinit var viewModel: AdminsViewModel
    lateinit var userToEdit: User

    override fun cancel() {
        viewModel.toggleDynamicPopUpScreen()
    }

    override fun confirm() {
        //confirm edit user
        val context = viewModel.getApplication<Application>().baseContext
        val loggedInUserHandler = LoggedInUserHandler(currentContext = context)

        viewModel.launchUpdateUser(authKey = loggedInUserHandler.getAuthKey())
    }

    fun setBaseValues(_userToEdit: User, _viewModel: AdminsViewModel) {
        userToEdit = _userToEdit
        viewModel = _viewModel
    }
}