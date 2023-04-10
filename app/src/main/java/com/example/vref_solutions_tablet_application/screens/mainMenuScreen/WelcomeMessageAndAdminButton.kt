package com.example.vref_solutions_tablet_application.screens.mainMenuScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.vref_solutions_tablet_application.handlers.LoggedInUserHandler
import com.example.vref_solutions_tablet_application.R
import com.example.vref_solutions_tablet_application.ScreenNavName
import com.example.vref_solutions_tablet_application.ui.theme.stylingClasses.*
import com.example.vref_solutions_tablet_application.ui.theme.stylingClasses.TextShadowStatic
import com.example.vref_solutions_tablet_application.viewModels.MainMenuViewModel

@Composable
fun WelcomeMessageAndAdminButton(viewModel: MainMenuViewModel, loggedInUserHandler: LoggedInUserHandler) {
    if(loggedInUserHandler.userIsAdmin() || loggedInUserHandler.userIsSuperAdmin()) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(top = MaterialTheme.padding.small),horizontalArrangement = Arrangement.End) {
            Button(contentPadding = PaddingValues(4.dp),
                shape = RoundedCornerShape(100),
                modifier = Modifier
                    .size(80.dp)
                    .shadow(4.dp, RoundedCornerShape(100)),
                onClick = { viewModel.navigateToPage(ScreenNavName.AdminMenu) }) {
                Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(Modifier.padding(top = MaterialTheme.padding.mini))

                    Image(
                        modifier = Modifier.size(MaterialTheme.iconSize.medium),
                        painter = painterResource(id = R.drawable.tools),
                        contentDescription = stringResource(R.string.cd_tools_icon)
                    )

                    Text(text = stringResource(R.string.admin_panel), color = Color.White, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                }
            }

            Spacer(Modifier.padding(end = MaterialTheme.padding.small))
        }
    }



    Text(text = "${stringResource(R.string.welcome)} \n${loggedInUserHandler.getFirstNameCurrentUser()}", textAlign = TextAlign.Center, fontSize = MaterialTheme.typography.h2.fontSize,
        color = MaterialTheme.colors.primary,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = MaterialTheme.padding.extraLarge),
        style = TextShadowStatic.Medium()
    )
}