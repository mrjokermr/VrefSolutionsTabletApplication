package com.example.vref_solutions_tablet_application.components.buttons

import android.app.DatePickerDialog
import android.content.Context
import android.widget.DatePicker
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.vref_solutions_tablet_application.models.DateSelectAndDisplayObject
import com.example.vref_solutions_tablet_application.R
import com.example.vref_solutions_tablet_application.ui.theme.stylingClasses.*


import com.example.vref_solutions_tablet_application.viewModels.MainMenuViewModel
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate

@Composable
fun FilterDateButton(dateDisplay: StateFlow<DateSelectAndDisplayObject>, viewModel: MainMenuViewModel, context: Context) {
    val dateDisplayState = dateDisplay.collectAsState()
    var displayDate by remember { mutableStateOf(dateDisplayState.value.displayDate) }
    var displayDay by remember { mutableStateOf(dateDisplayState.value.displayDay) }


    // Declaring DatePickerDialog and setting
    // initial values as current values (present year, month and day)
    val mDatePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
            //updated the remember values otherwise it wouldn't update its values
            dateDisplayState.value.setDateDisplay(LocalDate.of(mYear, mMonth + 1, mDayOfMonth))
            displayDate = dateDisplayState.value.displayDate
            displayDay = dateDisplayState.value.displayDay
            //sort the list by date
            viewModel.launchGetAllTrainingSummaries()
        }, dateDisplayState.value.date.year, dateDisplayState.value.date.monthValue - 1, dateDisplayState.value.date.dayOfMonth
    )

    dateDisplayState.value.setDateDisplay(dateDisplayState.value.date)

    Button(
        contentPadding = PaddingValues(4.dp),
        elevation = ButtonDefaults.elevation(0.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(MaterialTheme.roundedCornerShape.small))
            .background(MaterialTheme.colors.primary)
            .height(28.dp)
            .width(160.dp),
        onClick = {
            mDatePickerDialog.show()
        }) {

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(modifier = Modifier.weight(2f),text = "${dateDisplayState.value.displayText}:", color = Color.White, fontWeight = FontWeight.Bold,
                fontSize = MaterialTheme.typography.h6.fontSize)

            Text(modifier = Modifier.weight(3f),text = "${displayDate}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = MaterialTheme.typography.h6.fontSize)

            Box(modifier = Modifier.weight(1f)) {
                Image(
                    painter = painterResource(id = R.drawable.calendar_fill),
                    contentDescription = stringResource(R.string.cd_calendar_icon),
                    modifier = Modifier.size(MaterialTheme.iconSize.small)
                )

                //MaterialTheme.colors.primary
                Text(text = "${dateDisplayState.value.displayDay}",color = MaterialTheme.colors.primary, fontWeight = FontWeight.Bold ,fontSize = MaterialTheme.typography.subtitle1.fontSize,
                    modifier = Modifier.padding(top = 5.dp, start = 3.dp))

            }
        }
    }
}