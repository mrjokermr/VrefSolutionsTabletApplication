package com.example.vref_solutions_tablet_application.Components

import androidx.compose.runtime.*
import com.example.vref_solutions_tablet_application.Models.PopUpModels.EditUser
import com.example.vref_solutions_tablet_application.ViewModels.AdminsViewModel

@Composable
fun EditUserComponent(editUserHandler: EditUser, viewModel: AdminsViewModel) {
    editUserHandler.SetBaseValues(_userToEdit = viewModel.userToEdit.value, _viewModel = viewModel)

    NewOrEditUserInputComponent(viewModel = viewModel, isNewUserVariant = false)

}