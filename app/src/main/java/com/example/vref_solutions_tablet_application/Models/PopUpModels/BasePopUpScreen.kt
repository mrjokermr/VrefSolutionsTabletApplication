package com.example.vref_solutions_tablet_application.models.popUpModels

import androidx.compose.ui.unit.Dp
import com.example.vref_solutions_tablet_application.enums.PopUpType

abstract class BasePopUpScreen {
    abstract val height: Dp
    abstract val width: Dp

    abstract val title: Int
    abstract val confirmText: Int
    abstract val cancelText: Int

    abstract val type: PopUpType

    abstract fun cancel()

    abstract fun confirm()
}