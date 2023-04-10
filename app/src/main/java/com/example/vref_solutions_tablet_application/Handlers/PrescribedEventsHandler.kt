package com.example.vref_solutions_tablet_application.handlers

import com.example.vref_solutions_tablet_application.models.PrescribedEvent
import com.example.vref_solutions_tablet_application.R

object PrescribedEventsHandler {

    fun getDrawableIdBySymbolName(symbolName: String): Int {
        allPescribedEvents.forEach {
            if(it.symbolName == symbolName) return it.drawableId
        }

        //not found return not found icon
        return R.drawable.fb_ico_not_found
    }

    val allPescribedEvents: List<PrescribedEvent> = listOf(

        PrescribedEvent(
            title = "Landing flaire",
            message = "Start earlier with the landing flare manoeuvre",
            drawableId = R.drawable.fb_prescribed_landing_warning,
            symbolName = "fb_prescribed_[landing_warning]"
        ),

        PrescribedEvent(
            title = "Nice take-off rotation",
            message = "Nice and calm performed the take-off rotation",
            drawableId = R.drawable.fb_prescribed_airplane_rotation,
            symbolName = "fb_prescribed_[airplane_rotation]"
        ),

        PrescribedEvent(
            title = "Pay attention to the failure management",
            message = "Pay attention to the structure from the failure management when emergency procedure is active",
            drawableId = R.drawable.fb_prescribed_dashboard_warning,
            symbolName = "fb_prescribed_[dashboard_warning]"
        ),

        PrescribedEvent(
            title = "Make small adjustments",
            message = "Make calm and small adjustments while flying",
            drawableId = R.drawable.fb_prescribed_yoke_warning,
            symbolName = "fb_prescribed_[yoke_warning]"
        ),

        PrescribedEvent(
            title = "Not stable = Go-around",
            message = "The airplane was not stable enough to for the final approach",
            drawableId = R.drawable.fb_prescribed_abort_landing,
            symbolName = "fb_prescribed_[abort_landing]"
        ),

        PrescribedEvent(
            title = "Flown nicely and stable",
            message = "Has been flown nicely and stable",
            drawableId = R.drawable.fb_prescribed_airplane_thumb_up,
            symbolName = "fb_prescribed_[airplane_thumb_up]"
        ),

        PrescribedEvent(
            title = "Pay attention to the airplane speed",
            message = "Pay attention to the airplane speed when approaching the landing strip",
            drawableId = R.drawable.fb_prescribed_speed_warning,
            symbolName = "fb_prescribed_[speed_warning]"
        ),


        )
}