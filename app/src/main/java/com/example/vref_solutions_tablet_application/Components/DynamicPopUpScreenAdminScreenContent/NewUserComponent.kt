package com.example.vref_solutions_tablet_application.Components

import androidx.compose.runtime.Composable
import com.example.vref_solutions_tablet_application.Models.PopUpModels.NewUser
import com.example.vref_solutions_tablet_application.ViewModels.AdminsViewModel

@Composable
fun NewUserComponent(newUserHandler: NewUser, viewModel: AdminsViewModel) {
    newUserHandler.viewModel = viewModel
    viewModel.ResetUserToEdit()
    
    NewOrEditUserInputComponent(viewModel = viewModel, isNewUserVariant = true)
    //the same screen for new user and edit user is used, so reset the data that could have been set by
    //previously editing a user

}