package com.example.vref_solutions_tablet_application.components.buttons

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.vref_solutions_tablet_application.R
import com.example.vref_solutions_tablet_application.ui.theme.stylingClasses.*



@Composable
fun CurrentEventsListFilterButton(initialFilterValue: Boolean,buttonText: String, onClickAction: ()->Unit) {
    var filterIsActive by remember { mutableStateOf(initialFilterValue) }

    Button(
        onClick = {
            //change its displayed state and change the events list
            filterIsActive = !filterIsActive
            onClickAction()
        },
        contentPadding = PaddingValues(start = MaterialTheme.padding.tiny, end = MaterialTheme.padding.tiny),
        shape = RoundedCornerShape(38),
        colors = ButtonDefaults.buttonColors(backgroundColor = if(filterIsActive) MaterialTheme.colors.primary else Color(0xFF856699)),
        modifier = Modifier.height(22.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = buttonText,
                color = Color.White,
                fontSize = MaterialTheme.typography.h6.fontSize,
            )

            Spacer(Modifier.padding(start = 4.dp, end = 2.dp))

            Image(
                painter = painterResource(id = if(filterIsActive) R.drawable.check_lg else R.drawable.x_lg),
                contentDescription = stringResource(R.string.cd_check_icon),
                colorFilter = ColorFilter.tint(Color.White),
                modifier = Modifier.size(MaterialTheme.iconSize.tiny)
            )
        }
    }
}