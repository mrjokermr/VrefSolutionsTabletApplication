package com.example.vref_solutions_tablet_application.Components

import android.app.Application
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.example.vref_solutions_tablet_application.Components.TextField.RegularTextFieldWithPlaceholder
import com.example.vref_solutions_tablet_application.Components.`Text-UI`.SmallAditionalInfoText
import com.example.vref_solutions_tablet_application.Enums.UserType
import com.example.vref_solutions_tablet_application.Handlers.LoggedInUserHandler
import com.example.vref_solutions_tablet_application.R
import com.example.vref_solutions_tablet_application.StylingClasses.FontSizeStatic
import com.example.vref_solutions_tablet_application.StylingClasses.IconSizeStatic
import com.example.vref_solutions_tablet_application.StylingClasses.PaddingStatic
import com.example.vref_solutions_tablet_application.StylingClasses.RoundedSizeStatic
import com.example.vref_solutions_tablet_application.ViewModels.AdminsViewModel
import com.example.vref_solutions_tablet_application.ui.theme.PopUpBackgroundDarker
import com.example.vref_solutions_tablet_application.ui.theme.PopUpBoxDarkBackground

@Composable
fun NewOrEditUserInputComponent(viewModel: AdminsViewModel, isNewUserVariant: Boolean) {
    Column(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth()) {

            Column(modifier = Modifier.weight(1f)) {

                SmallAditionalInfoText(text = "Firstname", modifier = Modifier.fillMaxWidth(), textSize = FontSizeStatic.Tiny, textAlign = TextAlign.Left,
                    fontWeight = FontWeight.Normal)


                Spacer(Modifier.padding(PaddingStatic.Tiny))

                var firstNameState = viewModel.inputFirstname.collectAsState()

                RegularTextFieldWithPlaceholder(placeholderText = "First name here...",modifier = Modifier.fillMaxWidth().shadow(elevation = 12.dp), value = firstNameState,
                    onValueChangeFun = { viewModel.SetFirstNameInput(it) }, isPasswordDisplay = false, enabled = true)

            }

            Spacer(Modifier.padding(PaddingStatic.Small))

            Column(modifier = Modifier.weight(1f)) {
                SmallAditionalInfoText(text = "Lastname", modifier = Modifier.fillMaxWidth(), textSize = FontSizeStatic.Tiny, textAlign = TextAlign.Left,
                    fontWeight = FontWeight.Normal)

                Spacer(Modifier.padding(PaddingStatic.Tiny))

                //viewModel.SetLastNameInput(editUserHandler.userToEdit.lastName)
                var lastNameState = viewModel.inputLastName.collectAsState()

                RegularTextFieldWithPlaceholder(placeholderText = "Last name here...",modifier = Modifier.fillMaxWidth().shadow(elevation = 12.dp), value = lastNameState,
                    onValueChangeFun = { viewModel.SetLastNameInput(it) }, isPasswordDisplay = false, enabled = true)

            }

        } // end row with firstname and lastname

        Spacer(Modifier.padding(PaddingStatic.Small))

        SmallAditionalInfoText(text = "E-mail", modifier = Modifier.fillMaxWidth(), textSize = FontSizeStatic.Tiny, textAlign = TextAlign.Left,
            fontWeight = FontWeight.Normal)

        Spacer(Modifier.padding(PaddingStatic.Tiny))

        Row(modifier = Modifier.fillMaxWidth()) {
            var emailState = viewModel.inputEmail.collectAsState()

            RegularTextFieldWithPlaceholder(placeholderText = "youremailhere@email.com",modifier = Modifier.fillMaxWidth().shadow(elevation = 12.dp), value = emailState,
                onValueChangeFun = { viewModel.SetEmailInput(it) }, isPasswordDisplay = false, enabled = true)

            //Only when editing a user the type can be set, otherwise the default is Student
            if (isNewUserVariant == false) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(PaddingStatic.Tiny)
                ) {
                    UserTypeDropDownMenu(viewModel = viewModel)
                }
            }
        }

    }
}

@Composable
fun UserTypeDropDownMenu(viewModel: AdminsViewModel) {
    var loggedInUserHandler = LoggedInUserHandler(currentContext = viewModel.getApplication<Application>().baseContext)

    var expanded by remember { mutableStateOf(false) }

    var userTypesList: MutableList<UserType> = UserType.values().toMutableList()
    //remove the possibility for a admin to create superadmin users.
    if(loggedInUserHandler.UserIsAdmin()) userTypesList.remove(UserType.SuperAdmin)

    var selectedUserType = viewModel.inputUserType.collectAsState()

    var textFiledSize by remember { mutableStateOf(IntSize(0,0).toSize()) }

    Row(
        Modifier
            .fillMaxWidth()
            .height(52.dp)
            .padding(PaddingStatic.Mini)
            .background(
                color = PopUpBackgroundDarker,
                shape = RoundedCornerShape(RoundedSizeStatic.Small)
            )
            .onGloballyPositioned { coordinates -> textFiledSize = coordinates.size.toSize() }
            .clickable {
                //expand drop down menu
                expanded = true
            }, verticalAlignment = Alignment.CenterVertically) {

        Text(text = selectedUserType.value, color = Color.White, modifier = Modifier
            .weight(2f)
            .padding(start = PaddingStatic.Tiny),
            fontSize = FontSizeStatic.Small)

        Image(
            painter = painterResource(id = if(expanded) R.drawable.chevron_up else R.drawable.chevron_down),
            contentDescription = "cross_icon",
            modifier = Modifier
                .size(IconSizeStatic.Small)
                .weight(1f)
        )

    }

    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, Modifier.background(Color.White)) {
        var currentlySelectedValue = viewModel.inputUserType.collectAsState()

        for(userType in userTypesList) {
            DropdownMenuItem(
                onClick = {
                    viewModel.SetUserTypeInput(userType.toString())
                    expanded = false
                },
            ) {
                Text(userType.toString(), color = if(currentlySelectedValue.value == userType.toString()) MaterialTheme.colors.primary else Color.Black)
            }
        }
    }
}