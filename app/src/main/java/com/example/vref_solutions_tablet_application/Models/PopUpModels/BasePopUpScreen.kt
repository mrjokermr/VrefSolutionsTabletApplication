package com.example.vref_solutions_tablet_application.Models.PopUpModels

import androidx.compose.ui.unit.Dp
import com.example.vref_solutions_tablet_application.Enums.PopUpType

abstract class BasePopUpScreen {
    abstract val height: Dp
    abstract val width: Dp

    abstract val title: String
    abstract val confirmText: String
    abstract val cancelText: String

    abstract val type: PopUpType

    abstract fun Cancel()

    abstract fun Confirm()
}