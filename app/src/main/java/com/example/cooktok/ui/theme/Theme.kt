package com.example.cooktok.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = PrimaryRedOrange,
    secondary = PrimaryRedOrange,
    tertiary = PrimaryRedOrange
)

private val DarkColors = darkColorScheme(
    primary = PrimaryRedOrange,
    secondary = PrimaryRedOrange,
    tertiary = PrimaryRedOrange
)

@Composable
fun CookTokTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        shapes = AppShapes,

        content = content
    )
}
