package com.example.playlistmaker.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R


val ysDisplayFontFamily = FontFamily(
    Font(R.font.ys_display_regular, FontWeight.Light),
    Font(R.font.ys_display_medium, FontWeight.Medium)
)

val Typography = CustomTypography(
    titleMedium = TextStyle(
        fontFamily = ysDisplayFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp
    ),

    regular16 = TextStyle(
        fontFamily = ysDisplayFontFamily,
        fontWeight = FontWeight.Light,
        fontSize = 16.sp
    ),
    regular16Black = TextStyle(
        fontFamily = ysDisplayFontFamily,
        fontWeight = FontWeight.Light,
        fontSize = 16.sp,
        color = RichBlack
    ),
    regular11 = TextStyle(
        fontFamily = ysDisplayFontFamily,
        fontWeight = FontWeight.Light,
        fontSize = 11.sp
    ),
    medium14 = TextStyle(
        fontFamily = ysDisplayFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp
    ),
    medium19 = TextStyle(
        fontFamily = ysDisplayFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 19.sp
    ),
    regular12 = TextStyle(
        fontFamily = ysDisplayFontFamily,
        fontWeight = FontWeight.Light,
        fontSize = 12.sp,
        letterSpacing = 0.sp
    ),
    medium18 = TextStyle(
        fontFamily = ysDisplayFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp
    ),
)

data class CustomTypography(
    val titleMedium: TextStyle,
    val regular16: TextStyle,
    val regular16Black: TextStyle,
    val regular11: TextStyle,
    val medium14: TextStyle,
    val medium19: TextStyle,
    val regular12: TextStyle,
    val medium18: TextStyle
)