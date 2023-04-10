package com.example.vref_solutions_tablet_application.components


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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.example.vref_solutions_tablet_application.R
import com.example.vref_solutions_tablet_application.components.textField.RegularTextFieldWithPlaceholder
import com.example.vref_solutions_tablet_application.components.textUI.SmallAditionalInfoText
import com.example.vref_solutions_tablet_application.enums.UserType
import com.example.vref_solutions_tablet_application.handlers.LoggedInUserHandler
import com.example.vref_solutions_tablet_application.ui.theme.PopUpBackgroundDarker
import com.example.vref_solutions_tablet_application.viewModels.AdminsViewModel
import com.example.vref_solutions_tablet_application.ui.theme.stylingClasses.*

@Composable
fun NewOrEditUserInputComponent(viewModel: AdminsViewModel, isNewUserVariant: Boolean) {
    Column(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth()) {

            Column(modifier = Modifier.weight(1f)) {

                SmallAditionalInfoText(text = stringResource(R.string.firstname), modifier = Modifier.fillMaxWidth(), textSize = MaterialTheme.typography.h6.fontSize, textAlign = TextAlign.Left,
                    fontWeight = FontWeight.Normal)


                Spacer(Modifier.padding(MaterialTheme.padding.tiny))

                var firstNameState = viewModel.inputFirstname.collectAsState()

                RegularTextFieldWithPlaceholder(placeholderText = stringResource(R.string.firstname_here) + "...",modifier = Modifier.fillMaxWidth().shadow(elevation = 12.dp), value = firstNameState,
                    onValueChangeFun = { viewModel.setFirstNameInput(it) }, isPasswordDisplay = false, enabled = true)

            }

            Spacer(Modifier.padding(MaterialTheme.padding.small))

            Column(modifier = Modifier.weight(1f)) {
                SmallAditionalInfoText(text = stringResource(R.string.lastname), modifier = Modifier.fillMaxWidth(), textSize = MaterialTheme.typography.h6.fontSize, textAlign = TextAlign.Left,
                    fontWeight = FontWeight.Normal)

                Spacer(Modifier.padding(MaterialTheme.padding.tiny))

                //viewModel.SetLastNameInput(editUserHandler.userToEdit.lastName)
                var lastNameState = viewModel.inputLastName.collectAsState()

                RegularTextFieldWithPlaceholder(placeholderText = stringResource(R.string.lastname_here) + "...",modifier = Modifier.fillMaxWidth().shadow(elevation = 12.dp), value = lastNameState,
                    onValueChangeFun = { viewModel.setLastNameInput(it) }, isPasswordDisplay = false, enabled = true)

            }

        } // end row with firstname and lastname

        Spacer(Modifier.padding(MaterialTheme.padding.small))

        SmallAditionalInfoText(text = stringResource(R.string.email), modifier = Modifier.fillMaxWidth(), textSize = MaterialTheme.typography.h6.fontSize, textAlign = TextAlign.Left,
            fontWeight = FontWeight.Normal)

        Spacer(Modifier.padding(MaterialTheme.padding.tiny))

        Row(modifier = Modifier.fillMaxWidth()) {
            var emailState = viewModel.inputEmail.collectAsState()

            RegularTextFieldWithPlaceholder(placeholderText = stringResource(R.string.email_placeholder),modifier = Modifier.fillMaxWidth().shadow(elevation = 12.dp), value = emailState,
                onValueChangeFun = { viewModel.setEmailInput(it) }, isPasswordDisplay = false, enabled = true)

            //Only when editing a user the type can be set, otherwise the default is Student
            if (isNewUserVariant == false) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(MaterialTheme.padding.tiny)
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
    if(loggedInUserHandler.userIsAdmin()) userTypesList.remove(UserType.SuperAdmin)

    var selectedUserType = viewModel.inputUserType.collectAsState()

    var textFiledSize by remember { mutableStateOf(IntSize(0,0).toSize()) }

    Row(
        Modifier
            .fillMaxWidth()
            .height(52.dp)
            .padding(MaterialTheme.padding.mini)
            .background(
                color = PopUpBackgroundDarker,
                shape = RoundedCornerShape(MaterialTheme.roundedCornerShape.small)
            )
            .onGloballyPositioned { coordinates -> textFiledSize = coordinates.size.toSize() }
            .clickable {
                //expand drop down menu
                expanded = true
            }, verticalAlignment = Alignment.CenterVertically) {

        Text(text = selectedUserType.value, color = Color.White, modifier = Modifier
            .weight(2f)
            .padding(start = MaterialTheme.padding.tiny),
            fontSize = MaterialTheme.typography.h5.fontSize)

        Image(
            painter = painterResource(id = if(expanded) R.drawable.chevron_up else R.drawable.chevron_down),
            contentDescription = stringResource(R.string.cd_cross_icon),
            modifier = Modifier
                .size(MaterialTheme.iconSize.small)
                .weight(1f)
        )

    }

    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, Modifier.background(Color.White)) {
        var currentlySelectedValue = viewModel.inputUserType.collectAsState()

        for(userType in userTypesList) {
            DropdownMenuItem(
                onClick = {
                    viewModel.setUserTypeInput(userType.toString())
                    expanded = false
                },
            ) {
                Text(userType.toString(), color = if(currentlySelectedValue.value == userType.toString()) MaterialTheme.colors.primary else Color.Black)
            }
        }
    }
}