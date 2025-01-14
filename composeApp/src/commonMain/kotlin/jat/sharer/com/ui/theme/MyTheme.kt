package jat.sharer.com.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val DarkColorPalette = darkColors(
)
val LightColorPalette = lightColors(
    primary = Color(4, 82, 0), // green
    onPrimary = Color.White,// white
    secondary = Color(191, 28, 10),// red
    onSecondary = Color.White,//white
    background = Color.White,//white
    onBackground = Color.Black,
    surface = Color(249, 255, 196),//yellow
    onSurface = Color.Black
)

@Composable
fun JatSharerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = if (darkTheme) DarkColorPalette else LightColorPalette,
        content = content
    )
}