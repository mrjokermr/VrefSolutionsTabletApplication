package com.example.vref_solutions_tablet_application.Components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.vref_solutions_tablet_application.Components.Buttons.NegativeActionButton
import com.example.vref_solutions_tablet_application.Components.Buttons.RegularRectangleButton
import com.example.vref_solutions_tablet_application.Models.PopUpModels.AreYouSureInfo
import com.example.vref_solutions_tablet_application.R
import com.example.vref_solutions_tablet_application.StylingClasses.FontSizeStatic
import com.example.vref_solutions_tablet_application.StylingClasses.IconSizeStatic
import com.example.vref_solutions_tablet_application.StylingClasses.PaddingStatic
import com.example.vref_solutions_tablet_application.ui.theme.NegativeActionColor
import com.example.vref_solutions_tablet_application.ui.theme.PopUpBackgroundDarker

@Composable
fun AreYouSureContainer(areYouSureInfo: AreYouSureInfo, cancelAction: ()->Unit, confirmAction: ()->Unit) {
    Card(elevation = 12.dp, modifier = Modifier
        .height(areYouSureInfo.height)
        .width(areYouSureInfo.width)
        .padding(bottom = 40.dp),backgroundColor = PopUpBackgroundDarker) {
        Column(Modifier.padding(PaddingStatic.Tiny)) {
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top) {
                Text(text = areYouSureInfo.title, color = Color.White, fontSize = FontSizeStatic.Small)

                Image(
                    painter = painterResource(id = R.drawable.x_circle_fill),
                    contentDescription = "cross_icon",
                    modifier = Modifier
                        .size(IconSizeStatic.Small)
                        .clickable {
                            cancelAction()
                        }
                )
            }

            Spacer(Modifier.padding(PaddingStatic.Small))

            Text(modifier = Modifier.fillMaxWidth(), text = areYouSureInfo.explanation, color = Color.Gray, textAlign = TextAlign.Center)

            Spacer(Modifier.weight(1f))

            Row(Modifier.height(30.dp)) {

                RegularRectangleButton(buttonText = areYouSureInfo.cancelText, onClick = { cancelAction() }, modifier = Modifier.weight(1f),
                    fontSize = FontSizeStatic.Small, invertedColors = true)

                Spacer(Modifier.weight(1f))

                when(areYouSureInfo.isDiscardTraining) {
                    true -> NegativeActionButton(buttonText = areYouSureInfo.confirmText, onClick = { confirmAction() }, modifier = Modifier.weight(1f), fontSize = FontSizeStatic.Tiny)
                    false -> RegularRectangleButton(buttonText = areYouSureInfo.confirmText, onClick = { confirmAction() }, modifier = Modifier.weight(2f), fontSize = FontSizeStatic.Small, invertedColors = false)
                }
            }
        }

    }
}