package com.example.playlistmaker.settings.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.playlistmaker.R
import com.example.playlistmaker.root.ui.RootActivity
import com.example.playlistmaker.settings.ui.view_model.SettingsViewModel

@Composable
fun SettingsScreen(settingsViewModel: SettingsViewModel) {
    val context = LocalContext.current
    val activity = context as RootActivity

    val themeState by settingsViewModel.themeState.collectAsStateWithLifecycle()
    val systemIsDark = isSystemInDarkTheme()


    val currentIsDarkTheme = remember(themeState, systemIsDark) {
        when (themeState) {
            is ThemeState.NoSavedParams -> systemIsDark
            is ThemeState.SavedParamsExist ->
                (themeState as ThemeState.SavedParamsExist).isDarkMode
        }
    }
    LaunchedEffect(currentIsDarkTheme) {
        activity.switchTheme(currentIsDarkTheme)
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Title()
        SwitchTheme(
            isDarkTheme = currentIsDarkTheme,
            onThemeChanged = { checked -> settingsViewModel.enableDarkMode(checked) })
        SettingsRow(
            text = stringResource(R.string.share_text),
            iconRes = R.drawable.share,
            isDarkTheme = currentIsDarkTheme,
            onClick = { settingsViewModel.shareApp() })
        SettingsRow(
            text = stringResource(R.string.support_text),
            iconRes = R.drawable.ic_support,
            isDarkTheme = currentIsDarkTheme,
            onClick = { settingsViewModel.openSupport() })
        SettingsRow(
            text = stringResource(R.string.user_text),
            iconRes = R.drawable.ic_arrow_right,
            isDarkTheme = currentIsDarkTheme,
            onClick = { settingsViewModel.openTerms() })
    }
}


@Composable
private fun Title() {
    val textColor by animateColorAsState(
        targetValue = if (isSystemInDarkTheme()) colorResource(R.color.white) else colorResource(
            R.color.black
        )
    )

    Text(
        text = stringResource(R.string.button_settings_text),
        modifier = Modifier
            .height(56.dp)
            .padding(horizontal = 16.dp)
            .wrapContentHeight(Alignment.CenterVertically),
        fontSize = 22.sp,
        fontFamily = FontFamily(Font(R.font.ys_display_medium)),
        color = textColor
    )
}

@Composable
private fun SwitchTheme(
    isDarkTheme: Boolean, onThemeChanged: (Boolean) -> Unit
) {
    val textColor by animateColorAsState(
        targetValue = if (isDarkTheme) colorResource(R.color.white) else colorResource(
            R.color.black
        )
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.theme_text),
            modifier = Modifier.weight(1f),
            fontSize = 16.sp,
            fontFamily = FontFamily(Font(R.font.ys_display_regular)),
            color = textColor
        )
        Switch(
            checked = isDarkTheme,
            onCheckedChange = { onThemeChanged(!isDarkTheme) },
            colors = SwitchDefaults.colors(
                checkedThumbColor = colorResource(R.color.blue),
                uncheckedThumbColor = colorResource(R.color.silver_gray),
                checkedTrackColor = colorResource(R.color.blue),
                uncheckedTrackColor = colorResource(R.color.silver_gray)
            )
        )
    }
}

@Composable
private fun SettingsRow(
    text: String, iconRes: Int, isDarkTheme: Boolean, onClick: () -> Unit
) {
    val textColor by animateColorAsState(
        targetValue = if (isDarkTheme) colorResource(R.color.white) else colorResource(
            R.color.black
        )
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(62.dp)
            .padding(horizontal = 16.dp)
            .clickable(
                onClick = onClick
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            modifier = Modifier.weight(1f),
            fontSize = 16.sp,
            fontFamily = FontFamily(Font(R.font.ys_display_regular)),
            color = textColor
        )
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = null
        )
    }
}

@Composable
@Preview(showBackground = true)
fun SettingsScreenPreview() {
    SettingsRow(
        text = stringResource(R.string.share_text),
        iconRes = R.drawable.share,
        false,
        onClick = { }
    )
}