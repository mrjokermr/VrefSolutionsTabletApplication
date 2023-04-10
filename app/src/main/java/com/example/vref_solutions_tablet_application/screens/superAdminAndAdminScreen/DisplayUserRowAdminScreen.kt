package com.example.vref_solutions_tablet_application.screens.superAdminAndAdminScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.vref_solutions_tablet_application.models.popUpModels.EditUser
import com.example.vref_solutions_tablet_application.models.User
import com.example.vref_solutions_tablet_application.R
import com.example.vref_solutions_tablet_application.ui.theme.stylingClasses.*
import com.example.vref_solutions_tablet_application.ui.theme.stylingClasses.TextShadowStatic
import com.example.vref_solutions_tablet_application.viewModels.AdminsViewModel

@Composable
fun DisplayUserRowAdminScreen(user: User, viewModel: AdminsViewModel) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {

        Text(text = user.fullName(), color = Color.White, modifier = Modifier.weight(1f), fontSize = MaterialTheme.typography.h4.fontSize,style = TextShadowStatic.Small())
        Text(text = user.email, color = Color.White, modifier = Modifier.weight(1f), fontSize = MaterialTheme.typography.h4.fontSize,style = TextShadowStatic.Small())

        Spacer(Modifier.padding(MaterialTheme.padding.mini))

        Row(modifier = Modifier
            .weight(1f)
            .fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Text(text = user.userType.toString(), color = Color.White, modifier = Modifier.weight(3f), fontSize = MaterialTheme.typography.h4.fontSize,style = TextShadowStatic.Small())

            Button(
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
                modifier = Modifier.weight(1f),
                elevation = ButtonDefaults.elevation(
                    defaultElevation = 0.dp,
                    pressedElevation = 0.dp
                ),
                onClick = {
                    viewModel.setPopUpScreenType(EditUser())
                    viewModel.userToEdit.value = user
                    viewModel.toggleDynamicPopUpScreen()
                }
            ) {
                Image(
                    modifier = Modifier.size(MaterialTheme.iconSize.small),
                    painter = painterResource(id = R.drawable.pencil_purple),
                    contentDescription = stringResource(R.string.cd_edit_icon)
                )
            }
//            Row(modifier = Modifier.padding(top = MaterialTheme.padding.tiny, bottom = MaterialTheme.padding.tiny ,
//                start = MaterialTheme.padding.small, end = MaterialTheme.padding.small),
//                horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
//                Text(text = "New user", color = Color.White, fontWeight = FontWeight.Bold, fontSize = MaterialTheme.typography.h5.fontSize)
//                Spacer(Modifier.padding(MaterialTheme.padding.small))
//                Image(
//                    modifier = Modifier.size(MaterialTheme.iconSize.medium),
//                    painter = painterResource(id = R.drawable.plus),
//                    contentDescription = "plus icon"
//                )
//            }
        }

    }
}