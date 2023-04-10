package com.example.vref_solutions_tablet_application.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.vref_solutions_tablet_application.components.buttons.RegularRectangleButton
import com.example.vref_solutions_tablet_application.enums.IconNames
import com.example.vref_solutions_tablet_application.handlers.IconDisplayHandler
import com.example.vref_solutions_tablet_application.R
import com.example.vref_solutions_tablet_application.viewModels.LiveTrainingViewModel
import com.example.vref_solutions_tablet_application.ui.theme.PopUpBackground
import com.example.vref_solutions_tablet_application.ui.theme.PopUpBackgroundDarker
import com.example.vref_solutions_tablet_application.ui.theme.stylingClasses.*

@Composable
fun IconPickerPopUpComponent(viewModel: LiveTrainingViewModel) {
    AnimatedVisibility(modifier = Modifier.zIndex(17f),visible = true, enter = fadeIn(
        tween(200,
            easing = FastOutSlowInEasing
        )
    )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(17f), horizontalArrangement = Arrangement.Center
        ) {
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Card(elevation = 12.dp, modifier = Modifier
                    .height(620.dp)
                    .width(380.dp)
                    .padding(bottom = 40.dp),backgroundColor = PopUpBackgroundDarker) {
                    Column(modifier = Modifier
                        .fillMaxSize()
                        .padding(MaterialTheme.padding.tiny)) {
                        Row(modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(stringResource(R.string.icon_picker), color = Color.White, fontWeight = FontWeight.Bold)

                            Image(
                                painter = painterResource(id = R.drawable.x_circle_fill),
                                contentDescription = stringResource(R.string.cd_cross_icon),
                                modifier = Modifier
                                    .size(MaterialTheme.iconSize.small)
                                    .clickable {
                                        viewModel.toggleIconPickerPopUp(onCloseReset = true)
                                    }
                            )
                        }

                        //
                        val currentlySelectedIcon = viewModel.uiCurrentlySelectedIconForEvent.collectAsState()
                        Column(modifier = Modifier
                            .weight(4f)
                            .fillMaxWidth()) {
                            Text(text = stringResource(R.string.currently_selected_icon), modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, color = Color.White)

                            Spacer(Modifier.padding(MaterialTheme.padding.mini))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                                Column(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(MaterialTheme.roundedCornerShape.small))
                                        .size(70.dp)
                                        .background(PopUpBackground),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                )
                                {

                                    Image(
                                        painter = painterResource(
                                            id = if(currentlySelectedIcon.value == "")
                                                R.drawable.fb_ico_not_found else
                                                IconDisplayHandler.getPainterIdForSymbolName(currentlySelectedIcon.value))
                                                ,
                                        contentDescription = stringResource(R.string.cd_event_icon),
                                        modifier = Modifier.size(MaterialTheme.iconSize.large)
                                    )
                                }
                            }

                            Spacer(Modifier.padding(MaterialTheme.padding.tiny))

                        }

                        //Content
                        //LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.fillMaxSize()) {
                        Column(modifier = Modifier
                            .weight(7f)) {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(4),
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(MaterialTheme.padding.tiny),
                                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.padding.tiny)) {
                                var allPossibleIconNames: MutableList<String> = IconNames.values().map { it.toString() }.toMutableList()
                                items(allPossibleIconNames.size) {
                                    val symbolName = allPossibleIconNames[it]
                                    IconPickerItem(drawableId = IconDisplayHandler.getPainterIdForSymbolName(symbolName), symbolName = symbolName,viewModel = viewModel)
                                }
                            }
                        }

                        //buttons
                        Row(modifier = Modifier.weight(2f).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {

                            RegularRectangleButton(buttonText = stringResource(R.string.cancel), onClick = { viewModel.toggleIconPickerPopUp(onCloseReset = true) }, modifier = Modifier.width(100.dp),
                                fontSize = MaterialTheme.typography.h5.fontSize, invertedColors = true)

                            RegularRectangleButton(buttonText = stringResource(R.string.confirm), onClick = { viewModel.toggleIconPickerPopUp(onCloseReset = false) }, modifier = Modifier.width(100.dp),
                                fontSize = MaterialTheme.typography.h5.fontSize, invertedColors = false)

                        }
                    }

                }
            }
        }
    }
}

@Composable
fun IconPickerItem(drawableId: Int, symbolName: String,viewModel: LiveTrainingViewModel) {
    Card(modifier = Modifier.size(50.dp).clickable {
        viewModel.selectIconForEvent(symbolName)
    }, elevation = 8.dp) {
        Column(modifier = Modifier
            .clip(RoundedCornerShape(MaterialTheme.roundedCornerShape.small))
            .size(50.dp)
            .background(Color.White)
            , verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally)
        {

            Image(
                painter = painterResource(id = drawableId),
                colorFilter = ColorFilter.tint(Color(0xFF0d0d0d)),
                contentDescription = stringResource(R.string.cd_event_icon),
                modifier = Modifier.size(MaterialTheme.iconSize.medium)
            )
        }
    }

}