package com.example.vref_solutions_tablet_application.Components

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.example.vref_solutions_tablet_application.Handlers.CurrentTrainingHandler
import com.example.vref_solutions_tablet_application.Handlers.LoggedInUserHandler
import com.example.vref_solutions_tablet_application.R
import com.example.vref_solutions_tablet_application.ScreenNavName
import com.example.vref_solutions_tablet_application.Screens.DisplayStudent
import com.example.vref_solutions_tablet_application.StylingClasses.FontSizeStatic
import com.example.vref_solutions_tablet_application.StylingClasses.IconSizeStatic
import com.example.vref_solutions_tablet_application.StylingClasses.PaddingStatic
import com.example.vref_solutions_tablet_application.ViewModels.LiveTrainingViewModel
import com.example.vref_solutions_tablet_application.ViewModels.MainMenuViewModel
import kotlinx.coroutines.launch
import java.util.*

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun TopBarComp(currentScreen: ScreenNavName, navController: NavController, hideLogOutButton: Boolean) {
    val loggedInUserHandler: LoggedInUserHandler = LoggedInUserHandler(LocalContext.current)
    val currentTrainingHandler: CurrentTrainingHandler = CurrentTrainingHandler(LocalContext.current)

    val fullName = loggedInUserHandler.GetFullNameCurrentUser()
    val organisationName = loggedInUserHandler.GetUserOrganisationName()

    var firstStudentName by remember { mutableStateOf("") }
    var secondStudentName by remember { mutableStateOf("") }

    if(currentScreen == ScreenNavName.LiveTraining) {
        val viewModel = MainMenuViewModel(application = Application())

        viewModel.viewModelScope.launch {
            firstStudentName = viewModel.LoadStudentName(currentTrainingHandler.GetFirstStudentId().toLong(), authKey = currentTrainingHandler.GetAuthKey())
            secondStudentName = viewModel.LoadStudentName(currentTrainingHandler.GetSecondStudentId().toLong(), authKey = currentTrainingHandler.GetAuthKey())
        }
    }


    Box(
        modifier = Modifier.fillMaxWidth().height(80.dp).background(Color(0xFF292C32))
    ) {
        Row(Modifier.padding(PaddingStatic.Tiny)) {
            Image(painter = painterResource(id = R.drawable.mainlogo), contentDescription = "main_logo",
                modifier= Modifier.fillMaxHeight().weight(1.2f)
            )

            Row(modifier = Modifier.weight(4f).padding(start = PaddingStatic.Medium, end = PaddingStatic.Medium, top = PaddingStatic.Small),verticalAlignment = Alignment.CenterVertically) {
                if(currentScreen == ScreenNavName.LiveTraining) {
                    Column() {
                        Text(text = "Training", color = Color.White, fontWeight = FontWeight.Bold)
                        Text(text = "${currentTrainingHandler.GetCurrentTrainingCreationDate(topBarFormat = true)}", color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    Spacer(Modifier.padding(PaddingStatic.Small))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if(firstStudentName != "Unknown") DisplayStudent(firstStudentName)

                        Spacer(Modifier.padding(PaddingStatic.Mini))

                        if(secondStudentName != "Unknown") DisplayStudent(secondStudentName)
                    }

                    Spacer(Modifier.padding(PaddingStatic.Small))
                }
            }



            Row(modifier = Modifier.weight(2f), horizontalArrangement = Arrangement.Center) {
                Image(painter = painterResource(id = R.drawable.topbar_person), contentDescription = "main_logo",
                    modifier= Modifier.height(60.dp).weight(1f))

                Column(modifier = Modifier.weight(3f)) {
                    Text(text ="$fullName", color = MaterialTheme.colors.primaryVariant, fontSize = FontSizeStatic.Small, style = MaterialTheme.typography.h4.copy(
                        shadow = Shadow(
                            color = Color.Black,
                            offset = Offset(2f, 2f),
                            blurRadius = 8f
                        )
                    ))
                    Text(text = "$organisationName", color = MaterialTheme.colors.primaryVariant, fontSize = FontSizeStatic.Tiny,style = MaterialTheme.typography.h4.copy(
                        shadow = Shadow(
                            color = Color.Black,
                            offset = Offset(2f, 2f),
                            blurRadius = 8f
                        )
                    ))
                }

                if(!hideLogOutButton) {
                    Column(modifier = Modifier.weight(1f).fillMaxHeight(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                        //log out button/icon
                        Image(
                            painter = painterResource(id = R.drawable.log_out),
                            contentDescription = "log out icon",
                            modifier = Modifier.size(IconSizeStatic.Medium).clickable {
                                navController.navigate(ScreenNavName.Login.route) {
                                    popUpTo(0) //makes sure their is nothing else to pop back to
                                }
                            }
                        )
                    }
                }




            }
        }


    }

}