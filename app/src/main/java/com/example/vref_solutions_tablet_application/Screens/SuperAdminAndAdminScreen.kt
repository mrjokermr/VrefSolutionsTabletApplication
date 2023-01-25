package com.example.vref_solutions_tablet_application.Screens

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.ButtonDefaults.elevation
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.vref_solutions_tablet_application.Components.DynamicPopUpScreenAdmin
import com.example.vref_solutions_tablet_application.Components.TopBarComp
import com.example.vref_solutions_tablet_application.Handlers.LoggedInUserHandler
import com.example.vref_solutions_tablet_application.Models.PopUpModels.NewUser
import com.example.vref_solutions_tablet_application.Models.Organization
import com.example.vref_solutions_tablet_application.Models.PopUpModels.EditUser
import com.example.vref_solutions_tablet_application.Models.PopUpModels.NewOrganization
import com.example.vref_solutions_tablet_application.Models.User
import com.example.vref_solutions_tablet_application.R
import com.example.vref_solutions_tablet_application.ScreenNavName
import com.example.vref_solutions_tablet_application.StylingClasses.*
import com.example.vref_solutions_tablet_application.ViewModels.AdminsViewModel
import com.example.vref_solutions_tablet_application.ui.theme.NegativeActionColor
import com.example.vref_solutions_tablet_application.ui.theme.PopUpBackgroundDarker
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun SuperAdminAndAdminScreen(viewModel: AdminsViewModel = viewModel(), navController: NavController) {
    viewModel.navController = navController
    val currentContext = LocalContext.current
    viewModel.context = currentContext

    val loggedInUserHandler: LoggedInUserHandler = LoggedInUserHandler(currentContext = currentContext)
    val currentUserIsSuperAdmin = loggedInUserHandler.UserIsSuperAdmin()

    val currentlySelectedOrganization = viewModel.uiSelectedOrganization.collectAsState()

    viewModel.viewModelScope.launch {
        //viewModel.GetAllUsers(loggedInUserHandler.GetAuthKey())
        if(!currentUserIsSuperAdmin) {
            //because the user is not a super admin, the users list will be prefilled and can't be selected
            try {
                viewModel.GetAllUsersForOrganisation(authKey = loggedInUserHandler.GetAuthKey(), organisationId = loggedInUserHandler.GetCompanyIdCurrentUser().toLong())
            }
            catch(e: Throwable) {
                Toast.makeText(currentContext,"Couldn't load the users for your organisation, try again later.", Toast.LENGTH_LONG).show()
            }
        }
        else {
            viewModel.GetAllOrganisationsInfo(authKey = loggedInUserHandler.GetAuthKey())
        }
    }

    Scaffold(
        topBar = { TopBarComp(ScreenNavName.MainMenu, navController = navController,hideLogOutButton = false) },
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            if(currentUserIsSuperAdmin == false || currentlySelectedOrganization.value != null) {
                FloatingActionButton(
                    modifier = Modifier.zIndex(1f),
                    backgroundColor = MaterialTheme.colors.primary,
                    onClick = {
                        viewModel.SetPopUpScreenType(NewUser())
                        viewModel.ToggleDynamicPopUpScreen()
                    }) {

                    Row(modifier = Modifier
                        .padding(
                            top = PaddingStatic.Tiny, bottom = PaddingStatic.Tiny,
                            start = PaddingStatic.Small, end = PaddingStatic.Small
                        ),
                        horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "New user", color = Color.White, fontWeight = FontWeight.Bold, fontSize = FontSizeStatic.Small,style = TextShadowStatic.Small())
                        Spacer(Modifier.padding(PaddingStatic.Small))
                        Image(
                            modifier = Modifier.size(IconSizeStatic.Medium),
                            painter = painterResource(id = R.drawable.plus),
                            contentDescription = "plus icon"
                        )
                    }
                }
            }

        } ,
//        modifier = Modifier.padding(PaddingStatic.Small)
    ) {

        Row(Modifier.zIndex(1f)) {
            //If user is super admin display Column with organisations list

            //column with organisations list
            if(currentUserIsSuperAdmin) {
                Column(modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(topEnd = RoundedSizeStatic.Medium))
                    .fillMaxHeight()
                    .background(PopUpBackgroundDarker)) {

                    val allOrganisationInfo = viewModel.uiAllOrganizationsSummary.collectAsState()

                    Column(modifier = Modifier.weight(2f)) {
                        Button(
                            contentPadding = PaddingValues(4.dp),
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
                            elevation = elevation(
                                defaultElevation = 0.dp,
                                pressedElevation = 0.dp
                            ),
                            onClick = { viewModel.NavigateToPage(ScreenNavName.MainMenu) }) {

                            Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    painter = painterResource(id = R.drawable.chevron_left),
                                    contentDescription = "chevron left",
                                    modifier = Modifier.size(IconSizeStatic.Small)
                                )

                                Spacer(Modifier.padding(PaddingStatic.Mini))

                                Text(text = "Back", color = MaterialTheme.colors.primaryVariant,style = TextShadowStatic.Small(), fontSize = FontSizeStatic.Small)
                            }
                        }

                        Spacer(Modifier.padding(PaddingStatic.Tiny))

                        Text(text = "Organizations", fontWeight = FontWeight.Bold, textDecoration = TextDecoration.Underline, fontSize = FontSizeStatic.Normal, color = Color.White,
                            modifier = Modifier.padding(start = PaddingStatic.Tiny),style = TextShadowStatic.Small())
                    }

                    Spacer(Modifier.padding(PaddingStatic.Small))

                    LazyColumn(modifier = Modifier.weight(5f)) {
                        items(allOrganisationInfo.value.size) {
                            OrganisationSummaryListItem(organization = allOrganisationInfo.value[it], viewModel = viewModel, loggedInUserHandler = loggedInUserHandler)

                            Spacer(Modifier.padding(PaddingStatic.Mini))
                        }
                    }

                    Spacer(Modifier.padding(PaddingStatic.Small))

                    Column(modifier = Modifier.weight(1f).fillMaxWidth().padding(end = PaddingStatic.Tiny), horizontalAlignment = Alignment.End) {

                        Button(
                            onClick = {
                                viewModel.SetPopUpScreenType(NewOrganization())
                                viewModel.ToggleDynamicPopUpScreen()
                            },
                            contentPadding = PaddingValues(start = PaddingStatic.Tiny, end = PaddingStatic.Tiny),
                            shape = RoundedCornerShape(38),
                            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary ),
                            modifier = Modifier
                                .height(34.dp)

                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "New organization",
                                    color = MaterialTheme.colors.onSurface,
                                    fontSize = FontSizeStatic.AdminMenuButtonText,
                                    style = TextShadowStatic.Small()
                                )
                                Spacer(Modifier.padding(start = 8.dp, end = 4.dp))
                                Image(
                                    painter = painterResource(id = R.drawable.plus),
                                    contentDescription = "plus_icon",
                                    modifier = Modifier.size(IconSizeStatic.Tiny)
                                )
                            }
                        }

                    }

                }
            }


            //column with all the organisation management functionalities
            Column(modifier = Modifier
                .weight(3f)
                .fillMaxHeight()) {
                //back button here
                if(!currentUserIsSuperAdmin) {
                    Button(
                        contentPadding = PaddingValues(4.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
                        elevation = elevation(
                            defaultElevation = 0.dp,
                            pressedElevation = 0.dp
                        ),
                        onClick = { viewModel.NavigateToPage(ScreenNavName.MainMenu) }) {

                        Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.chevron_left),
                                contentDescription = "chevron left",
                                modifier = Modifier.size(IconSizeStatic.Small)
                            )

                            Spacer(Modifier.padding(PaddingStatic.Mini))

                            Text(text = "Back", color = MaterialTheme.colors.primaryVariant,style = TextShadowStatic.Small(),fontSize = FontSizeStatic.Small)
                        }
                    }
                }

                //Display users here
                Column(Modifier.padding(PaddingStatic.Small)) {

                    //edit company name here
                    Row() {

                        val initialCurrentlySelectedOrganizationName = viewModel.uiInitialOrganizationName.collectAsState()
                        val inputOrganizationName = viewModel.inputOrganizationName.collectAsState()
                        val focusRequester = remember { FocusRequester() }
                        val focusManager = LocalFocusManager.current

                        var textFieldIsFocussed by remember { mutableStateOf(false)}

                        TextField(
                            modifier = Modifier.focusRequester(focusRequester).onFocusChanged {
                                textFieldIsFocussed = it.isFocused
                            },
                            singleLine = true,
                            enabled =
                                if(currentUserIsSuperAdmin == false) true
                                else if(currentUserIsSuperAdmin && currentlySelectedOrganization.value == null) false
                                else true,
                            colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            disabledTextColor = Color.White,
                            textColor = Color.White,
                        ), textStyle = TextStyle(
                            fontSize = FontSizeStatic.Normal,
                            fontWeight = FontWeight.Bold,
                        ),
                            value = inputOrganizationName.value, onValueChange = {
                            viewModel.SetOrganizationNameInput(value = it, context = currentContext)
                        }, trailingIcon = {
                           if(currentUserIsSuperAdmin == false || (currentUserIsSuperAdmin && currentlySelectedOrganization.value != null)) {
                               if(inputOrganizationName.value == initialCurrentlySelectedOrganizationName.value) {
                                   Row() {
                                       if(textFieldIsFocussed) {
                                           Image(
                                               modifier = Modifier
                                                   .size(IconSizeStatic.Small)
                                                   .clickable {
                                                       //cancel change of organization name
                                                       focusManager.clearFocus()
                                                   },
                                               painter = painterResource(id = R.drawable.x_lg),
                                               contentDescription = "cancel icon"
                                           )
                                       }
                                       else {
                                           Image(
                                               modifier = Modifier
                                                   .size(IconSizeStatic.Small)
                                                   .zIndex(-1f)
                                                   .clickable {
                                                       focusRequester.requestFocus()
                                                   },
                                               painter = painterResource(id = R.drawable.pencil_purple),
                                               contentDescription = "edit icon"
                                           )
                                       }
                                   }

                               }
                               else
                                   Row() {
                                       Spacer(Modifier.padding(PaddingStatic.Tiny))

                                       Image(
                                           modifier = Modifier
                                               .size(IconSizeStatic.Small)
                                               .clickable {
                                                   //confirm change name of organization
                                                   if(currentUserIsSuperAdmin) {
                                                       if(currentlySelectedOrganization.value != null) {
                                                           try {
                                                               viewModel.viewModelScope.launch {
                                                                   viewModel.ConfirmChangingOrganizationName(
                                                                       organizationId = currentlySelectedOrganization.value!!.id,
                                                                       authKey = loggedInUserHandler.GetAuthKey(),
                                                                       context = currentContext)
                                                               }
                                                           }
                                                           catch(e: Throwable) {
                                                               Toast.makeText(currentContext, "Couldn't change the organization name try again later",Toast.LENGTH_LONG).show()
                                                           }
                                                       }
                                                   } else {
                                                       viewModel.viewModelScope.launch {
                                                           try {
                                                               viewModel.ConfirmChangingOrganizationName(
                                                                   organizationId = loggedInUserHandler.GetCompanyIdCurrentUser().toLong(),
                                                                   authKey = loggedInUserHandler.GetAuthKey(),
                                                                   context = currentContext
                                                               )
                                                           }
                                                           catch(e: Throwable) {
                                                               Toast.makeText(currentContext, "Couldn't change the organization name try again later",Toast.LENGTH_LONG).show()
                                                           }
                                                       }
                                                   }
                                                   focusManager.clearFocus()
                                               },
                                           painter = painterResource(id = R.drawable.check_lg),
                                           contentDescription = "check icon"
                                       )

                                       Spacer(Modifier.padding(PaddingStatic.Mini))

                                       Image(
                                           modifier = Modifier
                                               .size(IconSizeStatic.Small)
                                               .clickable {
                                                   //cancel change of organization name
                                                   viewModel.CancelChangingOrganizationName()
                                                   focusManager.clearFocus()
                                               },
                                           painter = painterResource(id = R.drawable.x_lg),
                                           contentDescription = "cancel icon"
                                       )
                                   }

                           }

                        }
                        )

                    }

                    Spacer(Modifier.padding(PaddingStatic.Small))

                    //display users part or display Organization & User management text
                    if(currentUserIsSuperAdmin == false || (currentUserIsSuperAdmin && currentlySelectedOrganization.value != null)) {
                        //user text title here
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text(text = "Users", fontSize = FontSizeStatic.Normal,color = Color.White, textDecoration = TextDecoration.Underline, fontWeight = FontWeight.Bold
                                ,style = TextShadowStatic.Small())
                        }

                        Spacer(Modifier.padding(PaddingStatic.Tiny))

                        //Row with Column-Titles
                        Row(Modifier.fillMaxWidth()) {
                            Text(text = "Fullname", color = Color.Gray, modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = FontSizeStatic.Normal
                                ,style = TextShadowStatic.Small())
                            Text(text = "E-mail", color = Color.Gray, modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = FontSizeStatic.Normal
                                ,style = TextShadowStatic.Small())

                            Spacer(Modifier.padding(PaddingStatic.Mini))

                            Text(text = "User Type", color = Color.Gray, modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = FontSizeStatic.Normal
                                ,style = TextShadowStatic.Small())
                        }

                        Spacer(Modifier.padding(PaddingStatic.Small))

                        val allUsers = viewModel.uiAllUsers.collectAsState()

                        //all user info
                        LazyColumn() {
                            items(allUsers.value.size) {
                                DisplayUserRowAdminScreen(viewModel = viewModel, user = allUsers.value[it])

                                Spacer(Modifier.padding(PaddingStatic.Mini))
                            }

                            item() {
                                //this part is for preventing that the new user button is overlapping the editing the last user in the list
                                Spacer(Modifier.padding(PaddingStatic.Medium))
                            }
                        }
                    }
                    else {
                        Text(text = "Organization & User Management", textAlign = TextAlign.Center, fontSize = FontSizeStatic.MediumTitle,
                            color = MaterialTheme.colors.primary,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = PaddingStatic.ExtraLarge)
                            ,style = TextShadowStatic.Small())

                    }

                }

            } // END column with all the organisation management functionalities

        } // END main content wrapper


    } // END scaffold wrapper


    var popUpScreenIsOpen = viewModel.uiPopUpScreenIsOpen.collectAsState()

    if(popUpScreenIsOpen.value) {
        Row(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.6f)), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
            DynamicPopUpScreenAdmin(viewModel = viewModel)
        }
        //EditUserPopUpScreen(viewModel = viewModel)
    }

} // END composable screen function


@Composable
fun OrganisationSummaryListItem(organization: Organization,viewModel: AdminsViewModel, loggedInUserHandler: LoggedInUserHandler) {
    val selectedOrganization = viewModel.uiSelectedOrganization.collectAsState()
    var thisButtonIsActive = false
    if(selectedOrganization.value != null) {
        thisButtonIsActive = organization == selectedOrganization.value
    }

    Row(modifier = Modifier
        .fillMaxWidth()
        .background(MaterialTheme.colors.onBackground)
        .padding(PaddingStatic.Tiny), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(text = organization.name, color = Color.White, fontWeight = FontWeight.Bold,style = TextShadowStatic.Small(), fontSize = FontSizeStatic.Small,
                modifier = Modifier.weight(4f))

        Button(
            onClick = {
                viewModel.SelectOrDeselectAndDisplayOrganisation(organization = organization, authKey = loggedInUserHandler.GetAuthKey())
            },
            contentPadding = PaddingValues(0.dp),
            shape = RoundedCornerShape(38),
            colors = ButtonDefaults.buttonColors(backgroundColor = if(thisButtonIsActive) NegativeActionColor else MaterialTheme.colors.primaryVariant ),
            modifier = Modifier.weight(3f).height(34.dp)

        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = if (thisButtonIsActive) R.drawable.x_circle_fill else R.drawable.pencil_white),
                    contentDescription = "eye_icon",
                    modifier = Modifier.size(IconSizeStatic.Tiny)
                )
                Spacer(Modifier.padding(start = 8.dp, end = 4.dp))
                Text(
                    text = if (thisButtonIsActive) "Close" else "Manage",
                    color = MaterialTheme.colors.onSurface,
                    fontSize = FontSizeStatic.AdminMenuButtonText,
                    style = TextShadowStatic.Small()
                )
            }
        }

    }
}


@Composable
fun DisplayUserRowAdminScreen(user: User, viewModel: AdminsViewModel) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {

        Text(text = user.FullName(), color = Color.White, modifier = Modifier.weight(1f), fontSize = FontSizeStatic.Normal,style = TextShadowStatic.Small())
        Text(text = user.email, color = Color.White, modifier = Modifier.weight(1f), fontSize = FontSizeStatic.Normal,style = TextShadowStatic.Small())

        Spacer(Modifier.padding(PaddingStatic.Mini))

        Row(modifier = Modifier
            .weight(1f)
            .fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Text(text = user.userType.toString(), color = Color.White, modifier = Modifier.weight(3f), fontSize = FontSizeStatic.Normal,style = TextShadowStatic.Small())

            Button(
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
                modifier = Modifier.weight(1f),
                elevation = elevation(
                    defaultElevation = 0.dp,
                    pressedElevation = 0.dp
                ),
                onClick = {
                    viewModel.SetPopUpScreenType(EditUser())
                    viewModel.userToEdit.value = user
                    viewModel.ToggleDynamicPopUpScreen()
                }
            ) {
                Image(
                    modifier = Modifier.size(IconSizeStatic.Small),
                    painter = painterResource(id = R.drawable.pencil_purple),
                    contentDescription = "edit icon"
                )
            }
//            Row(modifier = Modifier.padding(top = PaddingStatic.Tiny, bottom = PaddingStatic.Tiny ,
//                start = PaddingStatic.Small, end = PaddingStatic.Small),
//                horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
//                Text(text = "New user", color = Color.White, fontWeight = FontWeight.Bold, fontSize = FontSizeStatic.Small)
//                Spacer(Modifier.padding(PaddingStatic.Small))
//                Image(
//                    modifier = Modifier.size(IconSizeStatic.Medium),
//                    painter = painterResource(id = R.drawable.plus),
//                    contentDescription = "plus icon"
//                )
//            }
        }

    }
}