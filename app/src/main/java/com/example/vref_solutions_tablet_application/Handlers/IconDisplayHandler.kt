package com.example.vref_solutions_tablet_application.handlers

import com.example.vref_solutions_tablet_application.enums.IconNames
import com.example.vref_solutions_tablet_application.R

object IconDisplayHandler {

    fun getPainterIdForSymbolName(symbolName: String): Int {
        var iconId: Int = -1

        if(symbolName == "fb_manual") iconId = R.drawable.fb_manual_ico
        else if(symbolName == "fb_quick") iconId = R.drawable.fb_quick_ico
        else if(symbolName.contains("fb_auto")) iconId = R.drawable.fb_auto
        else {
            var allPossibleIconNames: MutableList<String> = IconNames.values().map { it.toString() }.toMutableList()

            for(icoName in allPossibleIconNames) {
                if(symbolName.contains(icoName) && icoName == "Thumb_up") {
                    return R.drawable.fb_thumb_up
                }
                else if(symbolName.contains(icoName) && icoName == "Thumb_down") {
                    return R.drawable.fb_thumb_down
                }
                else if(symbolName.contains(icoName) && icoName == "S") {
                    return R.drawable.fb_star
                }
                else if(symbolName.contains(icoName) && icoName == "Airplane") {
                    return R.drawable.fb_airplane
                }
                else if(symbolName.contains(icoName) && icoName == "Award") {
                    return R.drawable.fb_award
                }
                else if(symbolName.contains(icoName) && icoName == "Alert") {
                    return R.drawable.fb_alert
                }
                else if(symbolName.contains(icoName) && icoName == "Time") {
                    return R.drawable.fb_time
                }
                else if(symbolName.contains(icoName) && icoName == "Positive_weather") {
                    return R.drawable.fb_positive_weather
                }
                else if(symbolName.contains(icoName) && icoName == "Negative_weather") {
                    return R.drawable.fb_negative_weather
                }
                else if(symbolName.contains(icoName) && icoName == "Compass") {
                    return R.drawable.fb_compass
                }
                else if(symbolName.contains(icoName) && icoName == "Emoji_happy") {
                    return R.drawable.fb_emoji_happy
                }
                else if(symbolName.contains(icoName) && icoName == "Emoji_sad") {
                    return R.drawable.fb_emoji_sad
                }
                else if(symbolName.contains(icoName) && icoName == "Mistake") {
                    return R.drawable.fb_mistake
                }
                else if(symbolName.contains(icoName) && icoName == "Eye") {
                    return R.drawable.fb_eye
                }
                else if(symbolName.contains(icoName) && icoName == "Heart") {
                    return R.drawable.fb_heart
                }
            }

            iconId = R.drawable.fb_ico_not_found
        }

        return iconId
    }

}