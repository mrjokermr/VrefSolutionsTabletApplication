package com.example.vref_solutions_tablet_application.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import com.example.vref_solutions_tablet_application.ui.theme.stylingClasses.*
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val DarkColorPalette = darkColors(
    primary = Purple200,
    primaryVariant = Purple700,
    secondary = Teal200
)

private val LightColorPalette = lightColors(
    primary = Purple500,
    primaryVariant = Purple700,
    secondary = Teal200

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

private val VrefSolutionsDarkPalette = darkColors(
    //User is using DARK theme
    primary = MainPurple,
    primaryVariant = MainBlue,
    onPrimary = LighterBlue,
    secondary = SoftRed,
    background = MainBackground,
    onBackground = PopUpBackground,
    onSurface = Color.White
)

private val VrefSolutionsLightPalette = lightColors(
    //**TEMPORARY USE DARKCOLORS FOR LIGHTCOLORS BECAUSE THE EMULATOR (bluestack) DOESN'T SUPPOR CHANING DARK MODE
    primary = MainPurple,
    primaryVariant = MainBlue,
    onPrimary = LighterBlue,
    secondary = SoftRed,
    background = MainBackground,
    onBackground = PopUpBackground,
    onSurface = Color.White





//    //User is using Light theme
//    primary = MainPurple,
//    primaryVariant = MainBlue,
//    onPrimary = LighterBlue,
//    secondary = SoftRed,
//    background = WhiteBackground,
//    onBackground = PopUpBackground,
//    onSurface = Color.Black
)

@Composable
fun VrefSolutionsTabletApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        VrefSolutionsDarkPalette
    } else {
        VrefSolutionsLightPalette
    }

    //Change the status bar color by the "com.google.accompanist:accompanist-systemuicontroller:0.17.0" implementation
    val systemUiController = rememberSystemUiController()
    if(darkTheme){
        systemUiController.setSystemBarsColor(
            color = VrefSolutionsDarkPalette.background
        )
    }else{
        systemUiController.setSystemBarsColor(
            color = VrefSolutionsDarkPalette.background
        )
    }

    CompositionLocalProvider(
        LocalPadding provides Padding(),
        LocalRoundedCornerShape provides RoundedCornerShape(),
        LocalIconSize provides IconSize(),
    ) {
        MaterialTheme(
            colors = colors,
            typography = Typography,
            shapes = Shapes,
            content = content
        )
    }

}