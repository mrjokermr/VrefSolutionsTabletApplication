package com.example.vref_solutions_tablet_application.screens.superAdminAndAdminScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.vref_solutions_tablet_application.handlers.LoggedInUserHandler
import com.example.vref_solutions_tablet_application.models.Organization
import com.example.vref_solutions_tablet_application.R
import com.example.vref_solutions_tablet_application.ui.theme.stylingClasses.*
import com.example.vref_solutions_tablet_application.ui.theme.stylingClasses.TextShadowStatic
import com.example.vref_solutions_tablet_application.viewModels.AdminsViewModel
import com.example.vref_solutions_tablet_application.ui.theme.NegativeActionColor

@Composable
fun OrganisationSummaryListItem(organization: Organization, viewModel: AdminsViewModel, loggedInUserHandler: LoggedInUserHandler) {
    val selectedOrganization = viewModel.uiSelectedOrganization.collectAsState()
    var thisButtonIsActive = false
    if(selectedOrganization.value != null) {
        thisButtonIsActive = organization == selectedOrganization.value
    }

    Row(modifier = Modifier
        .fillMaxWidth()
        .background(MaterialTheme.colors.onBackground)
        .padding(MaterialTheme.padding.tiny), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(text = organization.name, color = Color.White, fontWeight = FontWeight.Bold,style = TextShadowStatic.Small(), fontSize = MaterialTheme.typography.h5.fontSize,
            modifier = Modifier.weight(4f))

        Button(
            onClick = {
                viewModel.selectOrDeselectAndDisplayOrganisation(organization = organization, authKey = loggedInUserHandler.getAuthKey())
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
                    modifier = Modifier.size(MaterialTheme.iconSize.tiny)
                )
                Spacer(Modifier.padding(start = 8.dp, end = 4.dp))
                Text(
                    text = if (thisButtonIsActive) "Close" else "Manage",
                    color = MaterialTheme.colors.onSurface,
                    fontSize = MaterialTheme.typography.body2.fontSize,
                    style = TextShadowStatic.Small()
                )
            }
        }

    }
}