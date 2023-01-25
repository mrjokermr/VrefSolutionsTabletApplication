package com.example.vref_solutions_tablet_application.Handlers

import com.example.vref_solutions_tablet_application.Enums.IconNames
import com.example.vref_solutions_tablet_application.R

class IconDisplayHandler {
    companion object {
        fun GetPainterIdForSymbolName(symbolName: String): Int {
            var iconId: Int = -1

            if(symbolName == "fb_manual") iconId = R.drawable.fb_manual_ico
            else if(symbolName == "fb_quick") iconId = R.drawable.fb_quick_ico
            else if(symbolName.contains("fb_auto")) iconId = R.drawable.fb_auto
            else {
                var allPossibleIconNames: MutableList<String> = IconNames.values().map { it.toString() }.toMutableList()

                for(icoName in allPossibleIconNames) {
                    if(symbolName.contains(icoName) && icoName == "thumb_up") {
                        return R.drawable.fb_thumb_up
                    }
                    else if(symbolName.contains(icoName) && icoName == "thumb_down") {
                        return R.drawable.fb_thumb_down
                    }
                    else if(symbolName.contains(icoName) && icoName == "star") {
                        return R.drawable.fb_star
                    }
                    else if(symbolName.contains(icoName) && icoName == "airplane") {
                        return R.drawable.fb_airplane
                    }
                    else if(symbolName.contains(icoName) && icoName == "award") {
                        return R.drawable.fb_award
                    }
                    else if(symbolName.contains(icoName) && icoName == "alert") {
                        return R.drawable.fb_alert
                    }
                    else if(symbolName.contains(icoName) && icoName == "time") {
                        return R.drawable.fb_time
                    }
                    else if(symbolName.contains(icoName) && icoName == "positive_weather") {
                        return R.drawable.fb_positive_weather
                    }
                    else if(symbolName.contains(icoName) && icoName == "negative_weather") {
                        return R.drawable.fb_negative_weather
                    }
                    else if(symbolName.contains(icoName) && icoName == "compass") {
                        return R.drawable.fb_compass
                    }
                    else if(symbolName.contains(icoName) && icoName == "emoji_happy") {
                        return R.drawable.fb_emoji_happy
                    }
                    else if(symbolName.contains(icoName) && icoName == "emoji_sad") {
                        return R.drawable.fb_emoji_sad
                    }
                    else if(symbolName.contains(icoName) && icoName == "mistake") {
                        return R.drawable.fb_mistake
                    }
                    else if(symbolName.contains(icoName) && icoName == "eye") {
                        return R.drawable.fb_eye
                    }
                    else if(symbolName.contains(icoName) && icoName == "heart") {
                        return R.drawable.fb_heart
                    }
                }

                iconId = R.drawable.fb_ico_not_found
            }

            return iconId
        }
    }
}