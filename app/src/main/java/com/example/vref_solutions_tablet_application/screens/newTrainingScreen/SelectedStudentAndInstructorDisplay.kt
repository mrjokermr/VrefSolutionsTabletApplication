package com.example.vref_solutions_tablet_application.screens.newTrainingScreen

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.example.vref_solutions_tablet_application.handlers.LoggedInUserHandler
import com.example.vref_solutions_tablet_application.ui.theme.stylingClasses.*
import com.example.vref_solutions_tablet_application.viewModels.NewTrainingViewModel

@Composable
fun SelectedStudentAndInstructorDisplay(viewModel: NewTrainingViewModel, loggedInUserHandler: LoggedInUserHandler) {
    val firstStudent = viewModel.uiSelectedStudentFirst.collectAsState()
    val secondStudent = viewModel.uiSelectedStudentSecond.collectAsState()

    if(firstStudent.value != null) {
        ShowcaseStudent(student = firstStudent.value!!, viewModel = viewModel)
    }
    else ShowcaseStudentPlaceholder(viewModel = viewModel)

    Spacer(Modifier.padding(MaterialTheme.padding.tiny))

    if(secondStudent.value != null) {
        ShowcaseStudent(student = secondStudent.value!!, viewModel = viewModel)
    }
    else ShowcaseStudentPlaceholder(viewModel = viewModel)

    Spacer(Modifier.padding(MaterialTheme.padding.small))

    ShowcaseInstructor(loggedInUserHandler = loggedInUserHandler)
}