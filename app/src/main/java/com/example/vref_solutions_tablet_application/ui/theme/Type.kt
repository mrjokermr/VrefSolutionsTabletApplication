package com.example.vref_solutions_tablet_application.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Set of Material typography styles to start with
val Typography = Typography(
    body1 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    h1 = TextStyle( //largeTitle
        fontSize = 72.sp,
    ),
    h2 = TextStyle( //mediumTitle
        fontSize = 54.sp,
    ),
    h3 = TextStyle( //large
        fontSize = 38.sp,
    ),
    h4 = TextStyle( //normal
        fontSize = 24.sp,
    ),
    h5 = TextStyle( //small
        fontSize = 18.sp,
    ),
    h6 = TextStyle( //tiny
        fontSize = 14.sp,
    ),
    subtitle1 = TextStyle( //mini
        fontSize = 10.sp,
    ),
    body2 = TextStyle( //AdminMenuButtonText
        fontSize = 16.sp,
    ),
)