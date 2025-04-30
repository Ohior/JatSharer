package jat.sharer.com.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.DismissValue
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import jat.sharer.com.core.ServerManager
import jat.sharer.com.rememberMultipleFilePickerLauncher
import jat.sharer.com.ui.TextIcon
import jat.sharer.com.ui.theme.PixelDensity
import jat.sharer.com.utils.Constants
import jatsharer.composeapp.generated.resources.Res
import jatsharer.composeapp.generated.resources.baseline_file_open_24
import jatsharer.composeapp.generated.resources.baseline_toggle_off_24
import jatsharer.composeapp.generated.resources.baseline_toggle_on_24
import org.jetbrains.compose.resources.painterResource

object HomeScreen : Screen {
    @Composable
    override fun Content() {
        Scaffold(
            modifier = Modifier
                .fillMaxSize(),
            topBar = {
                TopAppBar(title = {
                    Text(
                        text = "JatShare",
                        style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.Bold)
                    )
                })
            }
        ) { pv ->
            val homeViewModel = rememberScreenModel { HomeViewModel() }
            val serverState by ServerManager.listenToServer.collectAsState(false)
            LaunchedEffect(serverState) {
                if (serverState) {
                    ServerManager.startServer()
                } else {
                    ServerManager.stopServer()
                }
            }
            InFoPopup(homeViewModel.infoPopup) { homeViewModel.infoPopup = false }
            Column(modifier = Modifier.fillMaxSize().padding(pv)) {
                // DISPLAY FILES IF SELECTED ELSE DISPLAY INFO
                if (homeViewModel.deviceFiles.isEmpty()) {
                    InfoHalfScreen(Modifier.weight(1f)) { homeViewModel.infoPopup = true }
                } else {
                    FilesHalfScreen(
                        modifier = Modifier.weight(1f),
                        files = homeViewModel.deviceFiles,
                        onSwipe = { dv, df ->
                            if (dv == DismissValue.DismissedToStart) {
                                homeViewModel.deleteDeviceFiles(df)
                                true
                            } else false
                        },
                        onClick = { df ->
                            homeViewModel.downloadFile(df)
                        }
                    )
                }
                // DISPLAY START STOP SERVER BUTTON
                ServerButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colors.primary.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(
                                topEnd = PixelDensity.large,
                                topStart = PixelDensity.large
                            )
                        )
                        .padding(PixelDensity.extraLarge),
                    homeViewModel = homeViewModel,
                    active = serverState
                ) {
                    ServerManager.listenToServer.value = !ServerManager.listenToServer.value
                }
            }
        }
    }


    @Composable
    private fun InFoPopup(infoPopup: Boolean, onDismiss: () -> Unit) {
        if (infoPopup) {
            AlertDialog(
                onDismissRequest = onDismiss,
                title = {
                    Text(
                        modifier = Modifier.fillMaxWidth().padding(vertical = PixelDensity.medium),
                        textAlign = TextAlign.Center,
                        text = "How to use",
                        style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.SemiBold)
                    )
                },
                text = {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = Constants.HOWTO,
                        style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.SemiBold)
                    )
                },
                buttons = {
                    TextIcon(
                        modifier = Modifier.fillMaxWidth()
                            .padding(PixelDensity.medium)
                            .clip(RoundedCornerShape(PixelDensity.extraLarge))
                            .background(MaterialTheme.colors.onSurface)
                            .padding(PixelDensity.medium)
                            .clickable { onDismiss() },
                        text = "Close",
                        style = MaterialTheme.typography.h6.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colors.surface
                        ),
                        leadingIcon = {
                            Icon(
                                Icons.Filled.Close,
                                contentDescription = null,
                                tint = MaterialTheme.colors.surface
                            )
                        })
                },
                backgroundColor = Color.LightGray,
                shape = RoundedCornerShape(
                    topStart = PixelDensity.medium,
                    topEnd = PixelDensity.medium
                )
            )
        }
    }

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    private fun ServerButton(
        modifier: Modifier,
        active: Boolean,
        homeViewModel: HomeViewModel,
        serverOnclick: () -> Unit,
    ) {
        // Pick files from Compose
        val launcher = rememberMultipleFilePickerLauncher { files ->
            // Handle picked files
            homeViewModel.makeDeviceFiles(files)
        }
        FlowRow(
            modifier = modifier,
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalArrangement = Arrangement.Center
        ) {
            // Start / Stop server
            TextIcon(
                modifier = Modifier
                    .padding(PixelDensity.verySmall)
                    .background(if (active) Color.Green else Color.Red, RoundedCornerShape(14f))
                    .clickable { serverOnclick() }
                    .padding(PixelDensity.small),
                text = "${if (active) "Stop" else "Start"} Server",
                style = MaterialTheme.typography.body2.copy(
                    color = MaterialTheme.colors.onPrimary,
                    fontWeight = FontWeight.Bold,
                ),
                leadingIcon = {
                    if (active) {
                        Icon(
                            painterResource(Res.drawable.baseline_toggle_on_24),
                            contentDescription = "Server is active"
                        )
                    } else {
                        Icon(
                            painterResource(Res.drawable.baseline_toggle_off_24),
                            contentDescription = "Server is inactive"
                        )
                    }
                }
            )
            // Select files
            TextIcon(
                modifier = Modifier
                    .padding(PixelDensity.verySmall)
                    .background(
                        if (active) MaterialTheme.colors.primary.copy(alpha = 0.4f) else MaterialTheme.colors.primary,
                        RoundedCornerShape(14f)
                    )
                    .clickable(enabled = !active, onClick = {
                        launcher.launch(true, listOf("*/*"))
                    })
                    .padding(PixelDensity.small),
                text = "Select Files",
                style = MaterialTheme.typography.body2.copy(
                    color = MaterialTheme.colors.onPrimary,
                    fontWeight = FontWeight.Bold,
                ),
                leadingIcon = {
                    Icon(
                        painter = painterResource(Res.drawable.baseline_file_open_24),
                        contentDescription = "Select Files"
                    )
                }
            )
        }
    }

}
