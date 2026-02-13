package com.example.playlistmaker.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext


private val LightColorScheme = lightColorScheme(
    onPrimary = White,
    onSecondary = White,
    background = White,
    onBackground = RichBlack,
    surface = White,
    onSurface = RichBlack,
    outline = SilverGray,
    surfaceVariant = SoftSilver
)

private val DarkColorScheme = darkColorScheme(
    onPrimary = White,
    onSecondary = Black,
    background = RichBlack,
    onBackground = White,
    surface = RichBlack,
    onSurface = White,
    outline = SilverGray,
    surfaceVariant = Color(0xFF2A2B32)
)

data class CustomColors(
    val textColor: Color,
    val backgroundColor: Color,
    val searchBg:Color,
    val searchIconTint: Color,
    val trackText: Color,
    val primaryColor: Color,
    val historyTitleColor: Color,
    val buttonTextColor: Color,
    val selectedContent: Color
)

private val DarkCustomColors = CustomColors(
    textColor = White,
    backgroundColor = RichBlack,
    searchBg = White,
    searchIconTint = RichBlack,
    trackText =White,
    primaryColor = White,
    historyTitleColor = White,
    buttonTextColor = RichBlack,
    selectedContent = White
)

private val LightCustomColors = CustomColors(
    textColor = Black,
    backgroundColor = White,
    searchBg = SoftSilver,
    searchIconTint = SilverGray,
    trackText = Black,
    primaryColor = SilverGray,
    historyTitleColor = RichBlack,
    buttonTextColor = White,
    selectedContent = RichBlack
)

val LocalCustomColors = staticCompositionLocalOf<CustomColors> {
    error("No CustomColors provided")
}

val LocalTypography = staticCompositionLocalOf<CustomTypography> {
    error("No Typography provided")
}


@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val customColors = if (darkTheme) DarkCustomColors else LightCustomColors

    CompositionLocalProvider(
        LocalCustomColors provides customColors,
        LocalTypography provides Typography
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content
        )
    }
}