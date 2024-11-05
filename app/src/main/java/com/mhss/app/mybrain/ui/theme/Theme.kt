package com.mhss.app.mybrain.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily


const val PrimaryLightAlpha = .25f

private val DarkColorPalette = darkColors(
    primary = PrimaryColor,
    primaryVariant = PrimaryDarkColor,
    secondary = SecondaryColor,
    surface = DarkGray,
    background = Color.Black,
    onSurface = Color.White,
    onBackground = Color.White
)

private val LightColorPalette = lightColors(
    primary = PrimaryColor,
    primaryVariant = PrimaryDarkColor,
    secondary = SecondaryColor,
    surface = DarkGray,
    background = Color.Black,
    onSurface = Color.White,
    onBackground = Color.White
)

@Composable
fun MyBrainTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    fontFamily: FontFamily = Poppins,
    content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }
    val typography = getTypography(fontFamily)
    MaterialTheme(
        colors = colors,
        typography = typography,
        shapes = Shapes,
        content = content
    )
}