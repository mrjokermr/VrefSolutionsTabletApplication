package com.example.vref_solutions_tablet_application.screens.liveTraningScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.vref_solutions_tablet_application.enums.PopUpType
import com.example.vref_solutions_tablet_application.models.popUpModels.AddEvent
import com.example.vref_solutions_tablet_application.models.popUpModels.ExitTraining
import com.example.vref_solutions_tablet_application.models.popUpModels.SwitchCamera
import com.example.vref_solutions_tablet_application.R
import com.example.vref_solutions_tablet_application.ui.theme.stylingClasses.iconSize

import com.example.vref_solutions_tablet_application.viewModels.LiveTrainingViewModel

@Composable
fun FloatingActionButtonsMenu(viewModel: LiveTrainingViewModel) {
    var menuIsOpen = viewModel.uiOptionMenuIsOpen.collectAsState()

    Row() {
        Spacer(Modifier.weight(1f))
        BoxWithConstraints(
            modifier = Modifier
                .width(200.dp)
                .fillMaxHeight()
        ) {
            //main large button
            Button(contentPadding = PaddingValues(0.dp),
                shape = RoundedCornerShape(100),
                modifier = Modifier
                    .size(60.dp)
                    .align(Alignment.BottomEnd)
                    .shadow(4.dp, RoundedCornerShape(100)),
                onClick = { viewModel.toggleOptionMenu() }) {
                Image(
                    modifier = Modifier.size(if(!menuIsOpen.value) MaterialTheme.iconSize.menuBurgerIco else MaterialTheme.iconSize.medium),
                    painter = painterResource(id = if(!menuIsOpen.value) R.drawable.menu_ico else R.drawable.x_lg_white),
                    contentDescription = stringResource(R.string.cd_menu_icon)
                )
            }

            if (menuIsOpen.value) {
                //exit training button
                Button(contentPadding = PaddingValues(0.dp),
                    shape = RoundedCornerShape(100),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 130.dp)
                        .size(50.dp)
                        .shadow(4.dp, RoundedCornerShape(100)),
                    onClick = { viewModel.openPopUpScreen(ExitTraining()) }) {

                    Image(
                        modifier = Modifier
                            .size(MaterialTheme.iconSize.menuExitIco)
                            .padding(start = 4.dp),
                        painter = painterResource(id = R.drawable.exit),
                        contentDescription = stringResource(R.string.cd_exit_icon)
                    )

                }

                //switch camera 2
                Button(contentPadding = PaddingValues(0.dp),
                    shape = RoundedCornerShape(100),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 100.dp, bottom = 52.dp)
                        .size(50.dp)
                        .shadow(4.dp, RoundedCornerShape(100)),
                    onClick = { viewModel.openPopUpScreen(SwitchCamera(PopUpType.SWITCH_CAMERA_2, viewModel = viewModel)) }) {
                    Image(
                        modifier = Modifier.size(MaterialTheme.iconSize.menuCameraIco),
                        painter = painterResource(id = R.drawable.switch_camera_2),
                        contentDescription = stringResource(R.string.cd_switch_camera_2_icon)
                    )
                }

                //switch camera 1
                Button(contentPadding = PaddingValues(0.dp),
                    shape = RoundedCornerShape(100),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 60.dp, bottom = 94.dp)
                        .size(50.dp)
                        .shadow(4.dp, RoundedCornerShape(100)),
                    onClick = {
                        viewModel.openPopUpScreen(SwitchCamera(PopUpType.SWITCH_CAMERA_1, viewModel = viewModel))

                    }) {
                    Image(
                        modifier = Modifier.size(MaterialTheme.iconSize.menuCameraIco),
                        painter = painterResource(id = R.drawable.switch_camera_1),
                        contentDescription = stringResource(R.string.cd_switch_camera_1_icon)
                    )
                }

                //plus icon
                Button(contentPadding = PaddingValues(0.dp),
                    shape = RoundedCornerShape(100),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 10.dp, bottom = 126.dp)
                        .size(50.dp)
                        .shadow(4.dp, RoundedCornerShape(100)),
                    onClick = { viewModel.openPopUpScreen(AddEvent()) }) {
                    Image(
                        modifier = Modifier.size(MaterialTheme.iconSize.menuPlusIco),
                        painter = painterResource(id = R.drawable.plus),
                        contentDescription = stringResource(R.string.cd_plus_icon)
                    )
                }
            }


        }
    }
}