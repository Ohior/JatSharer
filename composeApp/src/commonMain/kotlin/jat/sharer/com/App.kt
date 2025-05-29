package jat.sharer.com

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import cafe.adriel.voyager.navigator.Navigator
import jat.sharer.com.ui.screens.home.HomeScreen
import jat.sharer.com.ui.theme.JatSharerTheme
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
@Preview
fun App(permissions: (() -> Unit)? = null) {
    JatSharerTheme {
        LaunchedEffect(Unit) {
            if (permissions != null) {
                permissions()
            }
        }
        Navigator(HomeScreen)
    }
}