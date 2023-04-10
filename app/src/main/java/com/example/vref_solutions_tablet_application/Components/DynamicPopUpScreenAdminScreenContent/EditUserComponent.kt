package com.example.vref_solutions_tablet_application.components

import androidx.compose.runtime.*
import com.example.vref_solutions_tablet_application.models.popUpModels.EditUser
import com.example.vref_solutions_tablet_application.viewModels.AdminsViewModel

@Composable
fun EditUserComponent(editUserHandler: EditUser, viewModel: AdminsViewModel) {
    editUserHandler.setBaseValues(_userToEdit = viewModel.userToEdit.value, _viewModel = viewModel)

    NewOrEditUserInputComponent(viewModel = viewModel, isNewUserVariant = false)

}