package jat.sharer.com.ui.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object PixelDensity {
    val verySmall = 4.dp
    val small = 8.dp
    val medium = 16.dp
    val large = 24.dp
    val extraLarge = 32.dp

    fun createPixelDensity(value: Int): Dp = value.dp
}