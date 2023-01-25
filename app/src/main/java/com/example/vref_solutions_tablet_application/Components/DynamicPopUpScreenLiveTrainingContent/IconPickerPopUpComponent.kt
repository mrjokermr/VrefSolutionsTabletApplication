package com.example.vref_solutions_tablet_application.Components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.vref_solutions_tablet_application.Components.Buttons.RegularRectangleButton
import com.example.vref_solutions_tablet_application.Enums.IconNames
import com.example.vref_solutions_tablet_application.Enums.PopUpType
import com.example.vref_solutions_tablet_application.Handlers.IconDisplayHandler
import com.example.vref_solutions_tablet_application.R
import com.example.vref_solutions_tablet_application.StylingClasses.FontSizeStatic
import com.example.vref_solutions_tablet_application.StylingClasses.IconSizeStatic
import com.example.vref_solutions_tablet_application.StylingClasses.PaddingStatic
import com.example.vref_solutions_tablet_application.StylingClasses.RoundedSizeStatic
import com.example.vref_solutions_tablet_application.ViewModels.LiveTrainingViewModel
import com.example.vref_solutions_tablet_application.ui.theme.PopUpBackground
import com.example.vref_solutions_tablet_application.ui.theme.PopUpBackgroundDarker

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
                        .padding(PaddingStatic.Tiny)) {
                        Row(modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Icon picker", color = Color.White, fontWeight = FontWeight.Bold)

                            Image(
                                painter = painterResource(id = R.drawable.x_circle_fill),
                                contentDescription = "cross_icon",
                                modifier = Modifier
                                    .size(IconSizeStatic.Small)
                                    .clickable {
                                        viewModel.ToggleIconPickerPopUp(onCloseReset = true)
                                    }
                            )
                        }

                        //
                        val currentlySelectedIcon = viewModel.uiCurrentlySelectedIconForEvent.collectAsState()
                        Column(modifier = Modifier
                            .weight(4f)
                            .fillMaxWidth()) {
                            Text(text = "Currently selected icon", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, color = Color.White)

                            Spacer(Modifier.padding(PaddingStatic.Mini))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                                Column(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(RoundedSizeStatic.Small))
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
                                                IconDisplayHandler.GetPainterIdForSymbolName(currentlySelectedIcon.value))
                                                ,
                                        contentDescription = "event_icon",
                                        modifier = Modifier.size(IconSizeStatic.Large)
                                    )
                                }
                            }

                            Spacer(Modifier.padding(PaddingStatic.Tiny))

                        }

                        //Content
                        //LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.fillMaxSize()) {
                        Column(modifier = Modifier
                            .weight(7f)) {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(4),
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(PaddingStatic.Tiny),
                                horizontalArrangement = Arrangement.spacedBy(PaddingStatic.Tiny)) {
                                var allPossibleIconNames: MutableList<String> = IconNames.values().map { it.toString() }.toMutableList()
                                items(allPossibleIconNames.size) {
                                    val symbolName = allPossibleIconNames[it]
                                    IconPickerItem(drawableId = IconDisplayHandler.GetPainterIdForSymbolName(symbolName), symbolName = symbolName,viewModel = viewModel)
                                }
                            }
                        }

                        //buttons
                        Row(modifier = Modifier.weight(2f).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {

                            RegularRectangleButton(buttonText = "Cancel", onClick = { viewModel.ToggleIconPickerPopUp(onCloseReset = true) }, modifier = Modifier.width(100.dp),
                                fontSize = FontSizeStatic.Small, invertedColors = true)

                            RegularRectangleButton(buttonText = "Confirm", onClick = { viewModel.ToggleIconPickerPopUp(onCloseReset = false) }, modifier = Modifier.width(100.dp),
                                fontSize = FontSizeStatic.Small, invertedColors = false)

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
        viewModel.SelectIconForEvent(symbolName)
    }, elevation = 8.dp) {
        Column(modifier = Modifier
            .clip(RoundedCornerShape(RoundedSizeStatic.Small))
            .size(50.dp)
            .background(Color.White)
            , verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally)
        {

            Image(
                painter = painterResource(id = drawableId),
                colorFilter = ColorFilter.tint(Color(0xFF0d0d0d)),
                contentDescription = "event_icon",
                modifier = Modifier.size(IconSizeStatic.Medium)
            )
        }
    }

}