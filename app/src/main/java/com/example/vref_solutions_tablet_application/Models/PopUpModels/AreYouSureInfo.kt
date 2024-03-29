package com.example.vref_solutions_tablet_application.models.popUpModels

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.Dp

//These string variables are loaded from string.xml on creation
class AreYouSureInfo(popUptitle: String, popUpexplanation: AnnotatedString, confirmActionText: String, cancelActionText: String, width: Dp, height: Dp,
                        isDiscardTraining: Boolean, isFinishTraining: Boolean, isDeleteUser: Boolean)
{
    val title: String = popUptitle
    val explanation: AnnotatedString = popUpexplanation
    val confirmText: String = confirmActionText
    val cancelText: String = cancelActionText
    val width: Dp = width
    val height: Dp = height
    val isDiscardTraining = isDiscardTraining
    val isFinishTraining = isFinishTraining
    val isDeleteUser = isDeleteUser
}