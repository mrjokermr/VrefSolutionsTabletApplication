package com.example.vref_solutions_tablet_application.screens

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
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.vref_solutions_tablet_application.components.DynamicPopUpScreenAdmin
import com.example.vref_solutions_tablet_application.components.TopBarComp
import com.example.vref_solutions_tablet_application.handlers.LoggedInUserHandler
import com.example.vref_solutions_tablet_application.models.popUpModels.NewUser
import com.example.vref_solutions_tablet_application.models.popUpModels.NewOrganization
import com.example.vref_solutions_tablet_application.R
import com.example.vref_solutions_tablet_application.ScreenNavName
import com.example.vref_solutions_tablet_application.screens.superAdminAndAdminScreen.DisplayUserRowAdminScreen
import com.example.vref_solutions_tablet_application.screens.superAdminAndAdminScreen.OrganisationSummaryListItem
import com.example.vref_solutions_tablet_application.viewModels.AdminsViewModel
import com.example.vref_solutions_tablet_application.ui.theme.PopUpBackgroundDarker



import com.example.vref_solutions_tablet_application.ui.theme.stylingClasses.TextShadowStatic
import com.example.vref_solutions_tablet_application.ui.theme.stylingClasses.*

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun SuperAdminAndAdminScreen(viewModel: AdminsViewModel = viewModel(), navController: NavController) {
    viewModel.navController = navController
    val currentContext = LocalContext.current

    val loggedInUserHandler: LoggedInUserHandler = LoggedInUserHandler(currentContext = currentContext)
    val currentUserIsSuperAdmin = loggedInUserHandler.userIsSuperAdmin()

    val currentlySelectedOrganization = viewModel.uiSelectedOrganization.collectAsState()

    viewModel.launchInitialAdminScreenSetup(currentUserIsSuperAdmin =  currentUserIsSuperAdmin, loggedInUserHandler = loggedInUserHandler)

    Scaffold(
        topBar = { TopBarComp(ScreenNavName.MainMenu, navController = navController,hideLogOutButton = false) },
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            if(currentUserIsSuperAdmin == false || currentlySelectedOrganization.value != null) {
                FloatingActionButton(
                    modifier = Modifier.zIndex(1f),
                    backgroundColor = MaterialTheme.colors.primary,
                    onClick = {
                        viewModel.setPopUpScreenType(NewUser())
                        viewModel.toggleDynamicPopUpScreen()
                    }) {

                    Row(modifier = Modifier
                        .padding(
                            top = MaterialTheme.padding.tiny, bottom = MaterialTheme.padding.tiny,
                            start = MaterialTheme.padding.small, end = MaterialTheme.padding.small
                        ),
                        horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "New user", color = Color.White, fontWeight = FontWeight.Bold, fontSize = MaterialTheme.typography.h5.fontSize,style = TextShadowStatic.Small())
                        Spacer(Modifier.padding(MaterialTheme.padding.small))
                        Image(
                            modifier = Modifier.size(MaterialTheme.iconSize.medium),
                            painter = painterResource(id = R.drawable.plus),
                            contentDescription = "plus icon"
                        )
                    }
                }
            }

        } ,
//        modifier = Modifier.padding(MaterialTheme.padding.small)
    ) {

        Row(Modifier.zIndex(1f)) {
            //If user is super admin display Column with organisations list

            //column with organisations list
            if(currentUserIsSuperAdmin) {
                Column(modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(topEnd = MaterialTheme.roundedCornerShape.medium))
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
                            onClick = { viewModel.navigateToPage(ScreenNavName.MainMenu) }) {

                            Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    painter = painterResource(id = R.drawable.chevron_left),
                                    contentDescription = "chevron left",
                                    modifier = Modifier.size(MaterialTheme.iconSize.small)
                                )

                                Spacer(Modifier.padding(MaterialTheme.padding.mini))

                                Text(text = "Back", color = MaterialTheme.colors.primaryVariant,style = TextShadowStatic.Small(), fontSize = MaterialTheme.typography.h5.fontSize)
                            }
                        }

                        Spacer(Modifier.padding(MaterialTheme.padding.tiny))

                        Text(text = "Organizations", fontWeight = FontWeight.Bold, textDecoration = TextDecoration.Underline, fontSize = MaterialTheme.typography.h4.fontSize, color = Color.White,
                            modifier = Modifier.padding(start = MaterialTheme.padding.tiny),style = TextShadowStatic.Small())
                    }

                    Spacer(Modifier.padding(MaterialTheme.padding.small))

                    LazyColumn(modifier = Modifier.weight(5f)) {
                        items(allOrganisationInfo.value.size) {
                            OrganisationSummaryListItem(organization = allOrganisationInfo.value[it], viewModel = viewModel, loggedInUserHandler = loggedInUserHandler)

                            Spacer(Modifier.padding(MaterialTheme.padding.mini))
                        }
                    }

                    Spacer(Modifier.padding(MaterialTheme.padding.small))

                    Column(modifier = Modifier.weight(1f).fillMaxWidth().padding(end = MaterialTheme.padding.tiny), horizontalAlignment = Alignment.End) {

                        Button(
                            onClick = {
                                viewModel.setPopUpScreenType(NewOrganization())
                                viewModel.toggleDynamicPopUpScreen()
                            },
                            contentPadding = PaddingValues(start = MaterialTheme.padding.tiny, end = MaterialTheme.padding.tiny),
                            shape = RoundedCornerShape(38),
                            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary ),
                            modifier = Modifier
                                .height(34.dp)

                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "New organization",
                                    color = MaterialTheme.colors.onSurface,
                                    fontSize = MaterialTheme.typography.body2.fontSize,
                                    style = TextShadowStatic.Small()
                                )
                                Spacer(Modifier.padding(start = 8.dp, end = 4.dp))
                                Image(
                                    painter = painterResource(id = R.drawable.plus),
                                    contentDescription = "plus_icon",
                                    modifier = Modifier.size(MaterialTheme.iconSize.tiny)
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
                        onClick = { viewModel.navigateToPage(ScreenNavName.MainMenu) }) {

                        Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.chevron_left),
                                contentDescription = "chevron left",
                                modifier = Modifier.size(MaterialTheme.iconSize.small)
                            )

                            Spacer(Modifier.padding(MaterialTheme.padding.mini))

                            Text(text = "Back", color = MaterialTheme.colors.primaryVariant,style = TextShadowStatic.Small(),fontSize = MaterialTheme.typography.h5.fontSize)
                        }
                    }
                }

                //Display users here
                Column(Modifier.padding(MaterialTheme.padding.small)) {

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
                            fontSize = MaterialTheme.typography.h4.fontSize,
                            fontWeight = FontWeight.Bold,
                        ),
                            value = inputOrganizationName.value, onValueChange = {
                            viewModel.setOrganizationNameInput(value = it)
                        }, trailingIcon = {
                           if(currentUserIsSuperAdmin == false || (currentUserIsSuperAdmin && currentlySelectedOrganization.value != null)) {
                               if(inputOrganizationName.value == initialCurrentlySelectedOrganizationName.value) {
                                   Row() {
                                       if(textFieldIsFocussed) {
                                           Image(
                                               modifier = Modifier
                                                   .size(MaterialTheme.iconSize.small)
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
                                                   .size(MaterialTheme.iconSize.small)
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
                                       Spacer(Modifier.padding(MaterialTheme.padding.tiny))

                                       Image(
                                           modifier = Modifier
                                               .size(MaterialTheme.iconSize.small)
                                               .clickable {
                                                   //confirm change name of organization
                                                   if(currentUserIsSuperAdmin) {
                                                       if(currentlySelectedOrganization.value != null) {
                                                           try {
                                                               viewModel.launchConfirmChangingOrganizationName(organizationId = currentlySelectedOrganization.value!!.id, authKey = loggedInUserHandler.getAuthKey())
                                                           }
                                                           catch(e: Throwable) {
                                                               Toast.makeText(currentContext, "Couldn't change the organization name try again later",Toast.LENGTH_LONG).show()
                                                           }
                                                       }
                                                   } else {
                                                       try {
                                                           viewModel.launchConfirmChangingOrganizationName(organizationId = loggedInUserHandler.getCompanyIdCurrentUser().toLong(),
                                                               authKey = loggedInUserHandler.getAuthKey()
                                                           )
                                                       } catch (e: Throwable) {
                                                           Toast.makeText(currentContext, "Couldn't change the organization name try again later", Toast.LENGTH_LONG).show()
                                                       }

                                                   }
                                                   focusManager.clearFocus()
                                               },
                                           painter = painterResource(id = R.drawable.check_lg),
                                           contentDescription = "check icon"
                                       )

                                       Spacer(Modifier.padding(MaterialTheme.padding.mini))

                                       Image(
                                           modifier = Modifier
                                               .size(MaterialTheme.iconSize.small)
                                               .clickable {
                                                   //cancel change of organization name
                                                   viewModel.cancelChangingOrganizationName()
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

                    Spacer(Modifier.padding(MaterialTheme.padding.small))

                    //display users part or display Organization & User management text
                    if(currentUserIsSuperAdmin == false || (currentUserIsSuperAdmin && currentlySelectedOrganization.value != null)) {
                        //user text title here
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text(text = "Users", fontSize = MaterialTheme.typography.h4.fontSize,color = Color.White, textDecoration = TextDecoration.Underline, fontWeight = FontWeight.Bold
                                ,style = TextShadowStatic.Small())
                        }

                        Spacer(Modifier.padding(MaterialTheme.padding.tiny))

                        //Row with Column-Titles
                        Row(Modifier.fillMaxWidth()) {
                            Text(text = "Fullname", color = Color.Gray, modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = MaterialTheme.typography.h4.fontSize
                                ,style = TextShadowStatic.Small())
                            Text(text = "E-mail", color = Color.Gray, modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = MaterialTheme.typography.h4.fontSize
                                ,style = TextShadowStatic.Small())

                            Spacer(Modifier.padding(MaterialTheme.padding.mini))

                            Text(text = "User Type", color = Color.Gray, modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = MaterialTheme.typography.h4.fontSize
                                ,style = TextShadowStatic.Small())
                        }

                        Spacer(Modifier.padding(MaterialTheme.padding.small))

                        val allUsers = viewModel.uiAllUsers.collectAsState()

                        //all user info
                        LazyColumn() {
                            items(allUsers.value.size) {
                                DisplayUserRowAdminScreen(viewModel = viewModel, user = allUsers.value[it])

                                Spacer(Modifier.padding(MaterialTheme.padding.mini))
                            }

                            item() {
                                //this part is for preventing that the new user button is overlapping the editing the last user in the list
                                Spacer(Modifier.padding(MaterialTheme.padding.medium))
                            }
                        }
                    }
                    else {
                        Text(text = "Organization & User Management", textAlign = TextAlign.Center, fontSize = MaterialTheme.typography.h2.fontSize,
                            color = MaterialTheme.colors.primary,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = MaterialTheme.padding.extraLarge)
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

