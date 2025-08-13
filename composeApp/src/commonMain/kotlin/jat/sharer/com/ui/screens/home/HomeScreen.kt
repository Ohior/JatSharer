package jat.sharer.com.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import jat.sharer.com.core.ServerManager
import jat.sharer.com.models.ConnectionState
import jat.sharer.com.rememberJFilePicker
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
            val deviceFiles by homeViewModel.deviceFiles.collectAsState()
//            val collectHotspot by homeViewModel.hotspotManager.isHotspotOn().collectAsState(false)
            LaunchedEffect(serverState) {
                if (serverState) {
                    ServerManager.startServer()
                } else {
                    ServerManager.stopServer()
                }
            }
            InFoPopup(homeViewModel.infoPopup) { homeViewModel.infoPopup(false) }
            HotspotPopup(
                homeViewModel.hotspotPopup is ConnectionState.Load,
                onDismiss = {
                    homeViewModel.hotspotPopup = ConnectionState.Success("Hotspot was set")
                },
                onCancel = {
                    homeViewModel.hotspotPopup = ConnectionState.Failed("Hotspot was not set")
                },
                onClick = {
                    homeViewModel.hotspotManager.enableHotspot()
                    homeViewModel.hotspotPopup = ConnectionState.Success("Hotspot was set")
                }
            )

            Column(modifier = Modifier.fillMaxSize().padding(pv)) {
                // DISPLAY FILES IF SELECTED ELSE DISPLAY INFO
                if (deviceFiles.isEmpty()) {
                    InfoHalfScreen(Modifier.weight(1f)) { homeViewModel.infoPopup(true) }
                } else {
                    FilesHalfScreen(
                        modifier = Modifier.weight(1f).padding(top = PixelDensity.medium),
                        files = deviceFiles,
                        onSwipe = { dv, df ->
                            if (dv == DismissValue.DismissedToStart) {
                                homeViewModel.deleteDeviceFiles(df)
                                true
                            } else false
                        },
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
                    when (homeViewModel.hotspotPopup) {
                        is ConnectionState.Success -> {
                            ServerManager.listenToServer.value = !ServerManager.listenToServer.value
                        }

                        is ConnectionState.Load -> {
                            Unit
                        }

                        else -> {
                            homeViewModel.hotspotPopup = ConnectionState.Load
                        }
                    }
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


    @Composable
    private fun HotspotPopup(
        hotspotPopup: Boolean,
        onDismiss: () -> Unit,
        onCancel: () -> Unit,
        onClick: () -> Unit
    ) {
        if (hotspotPopup) {
            AlertDialog(
                onDismissRequest = onDismiss,
                title = {
                    Row(Modifier.fillMaxWidth().padding(vertical = PixelDensity.medium), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Hot Spot Settings",
                            style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.SemiBold)
                        )
                        IconButton(onClick = onDismiss){
                            Icon(
                                Icons.Filled.Close,
                                contentDescription = null,
                                tint = MaterialTheme.colors.onSurface
                            )
                        }
                    }
                },
                text = {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Your hotspot is not active, go to the settings and enable it. This will set up your device as a server for other device to connect to",
                        style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.SemiBold)
                    )
                },
                buttons = {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .padding(horizontal = PixelDensity.medium),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextIcon(
                            modifier = Modifier
                                .clip(RoundedCornerShape(PixelDensity.medium))
                                .background(MaterialTheme.colors.onSurface)
                                .padding(PixelDensity.small)
                                .clickable { onCancel() },
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
                        TextIcon(
                            modifier = Modifier
                                .clip(RoundedCornerShape(PixelDensity.medium))
                                .background(MaterialTheme.colors.primary)
                                .padding(PixelDensity.small)
                                .clickable { onClick() },
                            text = "Enable",
                            style = MaterialTheme.typography.h6.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colors.surface
                            ),
                            leadingIcon = {
                                Icon(
                                    Icons.Filled.Info,
                                    contentDescription = null,
                                    tint = MaterialTheme.colors.surface
                                )
                            })
                    }
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
        val launcher = rememberJFilePicker { files ->
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
                        launcher.launch(listOf("*/*"))
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
